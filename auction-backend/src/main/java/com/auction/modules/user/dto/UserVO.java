package com.auction.modules.user.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String realName;
    private String idCard;
    private Integer authStatus;
    private BigDecimal deposit;
    private Integer status;
    private Date createdAt;
}
