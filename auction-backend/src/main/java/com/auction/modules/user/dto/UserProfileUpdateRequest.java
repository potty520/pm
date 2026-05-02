package com.auction.modules.user.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String nickname;
    private String phone;
    private String email;
}
