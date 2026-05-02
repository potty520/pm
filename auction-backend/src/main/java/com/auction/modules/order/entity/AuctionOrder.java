package com.auction.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("auction_order")
public class AuctionOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long itemId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal finalPrice;
    /**
     * 0-待支付 1-已支付 2-已发货 3-已完成 4-已取消
     */
    private Integer status;
    private String paymentMethod;
    private Date paymentTime;
    private Date createdAt;
    private Date updatedAt;
}
