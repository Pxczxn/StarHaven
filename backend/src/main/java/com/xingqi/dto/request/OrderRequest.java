package com.xingqi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单请求对象
 */
@Data
public class OrderRequest {

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空")
    private String customerName;

    /**
     * 客户手机号
     */
    @NotBlank(message = "客户手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String customerPhone;

    /**
     * 房间ID
     */
    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /**
     * 入住日期 (格式: yyyy-MM-dd)
     */
    @NotBlank(message = "入住日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "入住日期格式不正确")
    private String checkInDate;

    /**
     * 离店日期 (格式: yyyy-MM-dd)
     */
    @NotBlank(message = "离店日期不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "离店日期格式不正确")
    private String checkOutDate;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 订单来源
     */
    private String source;

    /**
     * 备注
     */
    private String remark;
}
