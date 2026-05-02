package com.auction.modules.session.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("auction_session")
public class AuctionSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
