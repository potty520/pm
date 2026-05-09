package com.auction.modules.user.dto;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class UserDepositRequest {
    @NotNull(message = "保证金金额不能为空")
    @DecimalMin(value = "0.01", message = "保证金金额必须大于0")
    private BigDecimal amount;
}
