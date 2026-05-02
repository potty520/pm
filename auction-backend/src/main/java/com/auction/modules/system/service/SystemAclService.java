package com.auction.modules.system.service;

import com.auction.modules.system.entity.SysPermission;
import com.auction.modules.system.entity.SysRole;
import com.auction.modules.system.dto.UserRoleAssignmentVO;

import java.util.List;

public interface SystemAclService {
    SysRole createRole(SysRole role);
    List<SysRole> listRoles();
    SysPermission createPermission(SysPermission permission);
    List<SysPermission> listPermissions();
    void bindRoleToUser(Long userId, Long roleId);
    void unbindRoleFromUser(Long userId, Long roleId);
    void bindPermissionToRole(Long roleId, Long permissionId);
    List<UserRoleAssignmentVO> listUserRoleAssignments(List<Long> userIds);
}
