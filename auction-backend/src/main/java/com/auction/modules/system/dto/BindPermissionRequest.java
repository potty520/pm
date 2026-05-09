package com.auction.modules.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class BindPermissionRequest {
    @NotNull
    private Long roleId;
    @NotNull
    private Long permissionId;
}
