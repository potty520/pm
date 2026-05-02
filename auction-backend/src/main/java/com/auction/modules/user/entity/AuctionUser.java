package com.auction.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.math.BigDecimal;

@Data
@TableName("auction_user")
public class AuctionUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String realName;
    private String idCard;
    /**
     * 0-未认证 1-已认证 2-认证失败
     */
    private Integer authStatus;
    private BigDecimal deposit;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
