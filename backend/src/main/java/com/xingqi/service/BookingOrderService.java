package com.xingqi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.dto.request.OrderCancelRequest;
import com.xingqi.dto.request.OrderPaymentRequest;
import com.xingqi.dto.request.OrderRequest;
import com.xingqi.entity.BookingOrder;

/**
 * 订单服务接口
 */
public interface BookingOrderService {

    /**
     * 分页查询订单
     */
    Page<BookingOrder> page(Integer page, Integer pageSize, String keyword, String status, String startDate, String endDate);

    /**
     * 根据ID查询订单
     */
    BookingOrder getById(Long id);

    /**
     * 创建订单
     */
    BookingOrder create(OrderRequest request);

    /**
     * 更新订单
     */
    BookingOrder update(Long id, OrderRequest request);

    /**
     * 确认订单（pending -> reserved）
     */
    BookingOrder confirm(Long id);

    /**
     * 办理入住（reserved -> occupied）
     */
    BookingOrder checkIn(Long id);

    /**
     * 办理退房（occupied -> completed）
     */
    BookingOrder checkOut(Long id);

    /**
     * 取消订单
     */
    BookingOrder cancel(Long id, OrderCancelRequest request);

    /**
     * 记录收款
     */
    BookingOrder recordPayment(Long id, OrderPaymentRequest request);

    /**
     * 删除订单
     */
    void delete(Long id);
}
