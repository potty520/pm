package com.auction.modules.order.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class OrderPayRequest {
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}
