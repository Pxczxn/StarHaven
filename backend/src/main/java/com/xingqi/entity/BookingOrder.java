package com.xingqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("booking_order")
public class BookingOrder {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户 ID
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
     * 客户身份证号
     */
    private String customerIdNumber;

    /**
     * 房间 ID
     */
    private Long roomId;

    /**
     * 房间号
     */
    private String roomNo;

    /**
     * 入住日期
     */
    private LocalDate checkInDate;

    /**
     * 退房日期
     */
    private LocalDate checkOutDate;

    /**
     * 入住天数
     */
    private Integer nights;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 订单状态
     * pending: 待确认
     * reserved: 已预订
     * occupied: 已入住
     * checked_out: 已退房
     * cancelled: 已取消
     */
    private String status;

    /**
     * 支付状态
     * unpaid: 未支付
     * partial: 部分支付
     * paid: 已支付
     * refunded: 已退款
     */
    private String paymentStatus;

    /**
     * 订单来源
     * front_desk: 前台
     * phone: 电话
     * wechat: 微信
     * h5: H5
     * ctrip: 携程
     * meituan: 美团
     * other: 其他
     */
    private String source;

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
