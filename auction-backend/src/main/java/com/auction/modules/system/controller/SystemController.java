package com.auction.modules.system.controller;

import com.auction.common.result.Result;
import com.auction.common.utils.SecurityUtils;
import com.auction.modules.system.dto.BindPermissionRequest;
import com.auction.modules.system.dto.BindRoleRequest;
import com.auction.modules.system.dto.UserRoleAssignmentVO;
import com.auction.modules.system.entity.SysPermission;
import com.auction.modules.system.entity.SysRole;
import com.auction.modules.system.service.AuthzService;
import com.auction.modules.system.service.SystemAclService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Validated
public class SystemController {
    private final SystemAclService aclService;
    private final AuthzService authzService;

    @GetMapping("/roles")
    public Result<List<SysRole>> roles() {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        return Result.ok(aclService.listRoles());
    }

    @PostMapping("/roles")
    public Result<SysRole> createRole(@RequestBody SysRole role) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        return Result.ok(aclService.createRole(role));
    }

    @GetMapping("/permissions")
    public Result<List<SysPermission>> permissions() {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        return Result.ok(aclService.listPermissions());
    }

    @PostMapping("/permissions")
    public Result<SysPermission> createPermission(@RequestBody SysPermission permission) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        return Result.ok(aclService.createPermission(permission));
    }

    @PostMapping("/bind-role")
    public Result<Void> bindRole(@Valid @RequestBody BindRoleRequest request) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        aclService.bindRoleToUser(request.getUserId(), request.getRoleId());
        return Result.ok(null);
    }

    @PostMapping("/unbind-role")
    public Result<Void> unbindRole(@Valid @RequestBody BindRoleRequest request) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        aclService.unbindRoleFromUser(request.getUserId(), request.getRoleId());
        return Result.ok(null);
    }

    @PostMapping("/bind-permission")
    public Result<Void> bindPermission(@Valid @RequestBody BindPermissionRequest request) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        aclService.bindPermissionToRole(request.getRoleId(), request.getPermissionId());
        return Result.ok(null);
    }

    @GetMapping("/user-roles")
    public Result<List<UserRoleAssignmentVO>> userRoles(@RequestParam(required = false) String userIds) {
        if (!authzService.isAdmin(SecurityUtils.currentUserId())) return Result.fail("无管理员权限");
        List<Long> ids = null;
        if (userIds != null && !userIds.trim().isEmpty()) {
            ids = Arrays.stream(userIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
        return Result.ok(aclService.listUserRoleAssignments(ids));
    }
}
