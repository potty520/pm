package com.auction.modules.system.dto;

import lombok.Data;

@Data
public class UserRoleAssignmentVO {
    private Long userId;
    private Long roleId;
    private String roleCode;
    private String roleName;
}
