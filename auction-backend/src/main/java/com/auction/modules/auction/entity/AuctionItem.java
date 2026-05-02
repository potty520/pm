package com.auction.modules.auction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("auction_item")
public class AuctionItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Integer categoryId;
    private Long sessionId;
    private BigDecimal startPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private Long sellerId;
    private Integer status;
    private Date startTime;
    private Date endTime;
    private Integer viewCount;
    private String rejectReason;
}
