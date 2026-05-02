package com.auction.modules.auction.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuctionReviewRequest {
    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    private String rejectReason;
}
