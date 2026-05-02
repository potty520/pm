package com.auction.modules.auction.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class BidRequest {
    private Long userId;

    @javax.validation.constraints.NotNull(message = "出价金额不能为空")
    @DecimalMin(value = "0.01", message = "出价金额必须大于0")
    private BigDecimal bidAmount;
}
