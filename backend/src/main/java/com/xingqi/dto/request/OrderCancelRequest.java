package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 订单取消请求
 */
@Data
public class OrderCancelRequest {

    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;
}
