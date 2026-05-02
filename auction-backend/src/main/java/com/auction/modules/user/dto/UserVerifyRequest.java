package com.auction.modules.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserVerifyRequest {
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;
}
