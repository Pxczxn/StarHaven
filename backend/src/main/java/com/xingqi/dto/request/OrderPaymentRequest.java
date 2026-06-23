package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单收款请求
 */
@Data
public class OrderPaymentRequest {

    /**
     * 收款金额
     */
    @NotNull(message = "收款金额不能为空")
    private BigDecimal amount;

    /**
     * 支付方式
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}
