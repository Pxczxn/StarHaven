package com.xingqi.dto.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 收款请求
 */
@Data
public class PaymentRequest {

    /**
     * 收款金额
     */
    @NotNull(message = "收款金额不能为空")
    @DecimalMin(value = "0.01", message = "收款金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;
}
