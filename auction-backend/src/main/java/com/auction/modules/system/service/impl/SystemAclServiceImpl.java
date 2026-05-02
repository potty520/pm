package com.auction.modules.system.service.impl;

import com.auction.modules.system.entity.SysPermission;
import com.auction.modules.system.entity.SysRole;
import com.auction.modules.system.entity.SysRolePermission;
import com.auction.modules.system.entity.SysUserRole;
import com.auction.modules.system.dto.UserRoleAssignmentVO;
import com.auction.modules.system.mapper.SysPermissionMapper;
import com.auction.modules.system.mapper.SysRoleMapper;
import com.auction.modules.system.mapper.SysRolePermissionMapper;
import com.auction.modules.system.mapper.SysUserRoleMapper;
import com.auction.modules.system.service.SystemAclService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemAclServiceImpl implements SystemAclService {
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole createRole(SysRole role) {
        roleMapper.insert(role);
        return role;
    }

    @Override
    public List<SysRole> listRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByDesc(SysRole::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysPermission createPermission(SysPermission permission) {
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    public List<SysPermission> listPermissions() {
        return permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>().orderByDesc(SysPermission::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindRoleToUser(Long userId, Long roleId) {
        SysUserRole existing = userRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId).eq(SysUserRole::getRoleId, roleId).last("LIMIT 1"));
        if (existing == null) {
            SysUserRole relation = new SysUserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            userRoleMapper.insert(relation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindRoleFromUser(Long userId, Long roleId) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPermissionToRole(Long roleId, Long permissionId) {
        SysRolePermission existing = rolePermissionMapper.selectOne(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId).eq(SysRolePermission::getPermissionId, permissionId).last("LIMIT 1"));
        if (existing == null) {
            SysRolePermission relation = new SysRolePermission();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            rolePermissionMapper.insert(relation);
        }
    }

    @Override
    public List<UserRoleAssignmentVO> listUserRoleAssignments(List<Long> userIds) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        if (userIds != null && !userIds.isEmpty()) {
            wrapper.in(SysUserRole::getUserId, userIds);
        }
        List<SysUserRole> relations = userRoleMapper.selectList(wrapper);
        if (relations.isEmpty()) return List.of();

        Set<Long> roleIds = relations.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds));
        Map<Long, SysRole> roleMap = roles.stream().collect(Collectors.toMap(SysRole::getId, r -> r));

        return relations.stream().map(rel -> {
            UserRoleAssignmentVO vo = new UserRoleAssignmentVO();
            vo.setUserId(rel.getUserId());
            vo.setRoleId(rel.getRoleId());
            SysRole role = roleMap.get(rel.getRoleId());
            if (role != null) {
                vo.setRoleCode(role.getRoleCode());
                vo.setRoleName(role.getRoleName());
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
