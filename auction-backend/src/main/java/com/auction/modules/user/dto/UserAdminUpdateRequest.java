package com.auction.modules.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAdminUpdateRequest {
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    private Integer authStatus;
    private BigDecimal deposit;
}
