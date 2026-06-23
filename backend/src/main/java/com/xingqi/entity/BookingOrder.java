package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("booking_order")
public class BookingOrder {

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 客户手机号
     */
    private String customerPhone;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 入住日期
     */
    private String checkInDate;

    /**
     * 离店日期
     */
    private String checkOutDate;

    /**
     * 入住晚数
     */
    private Integer nights;

    /**
     * 订单总额
     */
    private BigDecimal totalAmount;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 支付方式：cash-现金, wechat-微信, alipay-支付宝, bank_card-银行卡, other-其他
     */
    private String paymentMethod;

    /**
     * 订单来源：front_desk-前台, phone-电话, wechat-微信, ctrip-携程, meituan-美团, h5-H5, other-其他
     */
    private String source;

    /**
     * 订单状态：pending-待确认, reserved-已预订, occupied-已入住, completed-已完成, cancelled-已取消, refunded-已退款
     */
    private String status;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
