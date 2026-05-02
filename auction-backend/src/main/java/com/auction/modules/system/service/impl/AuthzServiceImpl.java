package com.auction.modules.system.service.impl;

import com.auction.modules.system.entity.SysPermission;
import com.auction.modules.system.entity.SysRolePermission;
import com.auction.modules.system.entity.SysUserRole;
import com.auction.modules.system.mapper.SysPermissionMapper;
import com.auction.modules.system.mapper.SysRolePermissionMapper;
import com.auction.modules.system.mapper.SysUserRoleMapper;
import com.auction.modules.system.service.AuthzService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthzServiceImpl implements AuthzService {
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysPermissionMapper permissionMapper;

    @Override
    public boolean isAdmin(Long userId) {
        return hasPermission(userId, "admin:*");
    }

    @Override
    public boolean hasPermission(Long userId, String permCode) {
        if (userId == null) return false;
        if (userId == 1L) return true;

        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) return false;
        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());

        List<SysRolePermission> rolePerms = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().in(SysRolePermission::getRoleId, roleIds)
        );
        if (rolePerms.isEmpty()) return false;
        Set<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toSet());

        List<SysPermission> perms = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().in(SysPermission::getId, permIds)
        );
        Set<String> codes = perms.stream().map(SysPermission::getPermCode).collect(Collectors.toSet());
        return codes.contains("admin:*") || codes.contains(permCode);
    }
}
