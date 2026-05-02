package com.auction.common.config;

import com.auction.modules.system.entity.SysPermission;
import com.auction.modules.system.entity.SysRole;
import com.auction.modules.system.entity.SysRolePermission;
import com.auction.modules.system.entity.SysUserRole;
import com.auction.modules.system.mapper.SysPermissionMapper;
import com.auction.modules.system.mapper.SysRoleMapper;
import com.auction.modules.system.mapper.SysRolePermissionMapper;
import com.auction.modules.system.mapper.SysUserRoleMapper;
import com.auction.modules.user.entity.AuctionUser;
import com.auction.modules.user.mapper.AuctionUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AuctionUserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Value("${admin.bootstrap.username:admin}")
    private String adminUsername;

    @Value("${admin.bootstrap.password:admin123456}")
    private String adminPassword;

    @Value("${admin.bootstrap.reset-password:true}")
    private boolean resetPassword;

    @Override
    public void run(String... args) {
        try {
            bootstrapAdmin();
        } catch (Exception ex) {
            log.warn("Admin bootstrap skipped: {}", ex.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void bootstrapAdmin() {
        if (adminUsername == null || adminUsername.trim().isEmpty()) {
            return;
        }
        AuctionUser admin = userMapper.selectOne(new LambdaQueryWrapper<AuctionUser>()
                .eq(AuctionUser::getUsername, adminUsername).last("LIMIT 1"));

        if (admin == null) {
            admin = new AuctionUser();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setNickname("系统管理员");
            admin.setStatus(1);
            admin.setAuthStatus(1);
            admin.setDeposit(BigDecimal.ZERO);
            userMapper.insert(admin);
            log.info("Bootstrapped admin user: {} (id={})", adminUsername, admin.getId());
        } else if (resetPassword) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userMapper.updateById(admin);
            log.info("Reset admin password for user: {} (id={})", adminUsername, admin.getId());
        }

        SysRole role = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "admin").last("LIMIT 1"));
        if (role == null) {
            role = new SysRole();
            role.setRoleCode("admin");
            role.setRoleName("系统管理员");
            role.setStatus(1);
            roleMapper.insert(role);
        }

        SysPermission perm = permissionMapper.selectOne(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermCode, "admin:*").last("LIMIT 1"));
        if (perm == null) {
            perm = new SysPermission();
            perm.setPermCode("admin:*");
            perm.setPermName("超级管理员");
            permissionMapper.insert(perm);
        }

        SysUserRole ur = userRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, admin.getId()).eq(SysUserRole::getRoleId, role.getId()).last("LIMIT 1"));
        if (ur == null) {
            ur = new SysUserRole();
            ur.setUserId(admin.getId());
            ur.setRoleId(role.getId());
            userRoleMapper.insert(ur);
        }

        SysRolePermission rp = rolePermissionMapper.selectOne(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, role.getId()).eq(SysRolePermission::getPermissionId, perm.getId()).last("LIMIT 1"));
        if (rp == null) {
            rp = new SysRolePermission();
            rp.setRoleId(role.getId());
            rp.setPermissionId(perm.getId());
            rolePermissionMapper.insert(rp);
        }
    }
}

