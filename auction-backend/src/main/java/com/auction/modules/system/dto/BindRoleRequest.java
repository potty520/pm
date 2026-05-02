package com.auction.modules.system.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BindRoleRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long roleId;
}
