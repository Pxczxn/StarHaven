package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.OrderCancelRequest;
import com.xingqi.dto.request.OrderPaymentRequest;
import com.xingqi.dto.request.OrderRequest;
import com.xingqi.entity.BookingOrder;
import com.xingqi.service.BookingOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BookingOrderService orderService;

    /**
     * 分页查询订单
     */
    @GetMapping
    public ApiResponse<PageResponse<BookingOrder>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Page<BookingOrder> result = orderService.page(page, pageSize, keyword, status, startDate, endDate);
        return ApiResponse.success(PageResponse.of(result));
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/{id}")
    public ApiResponse<BookingOrder> getById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getById(id));
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<BookingOrder> create(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.create(request));
    }

    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public ApiResponse<BookingOrder> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.update(id, request));
    }

    /**
     * 确认订单
     */
    @PutMapping("/{id}/confirm")
    public ApiResponse<BookingOrder> confirm(@PathVariable Long id) {
        return ApiResponse.success(orderService.confirm(id));
    }

    /**
     * 办理入住
     */
    @PutMapping("/{id}/check-in")
    public ApiResponse<BookingOrder> checkIn(@PathVariable Long id) {
        return ApiResponse.success(orderService.checkIn(id));
    }

    /**
     * 办理退房
     */
    @PutMapping("/{id}/check-out")
    public ApiResponse<BookingOrder> checkOut(@PathVariable Long id) {
        return ApiResponse.success(orderService.checkOut(id));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<BookingOrder> cancel(@PathVariable Long id, @Valid @RequestBody OrderCancelRequest request) {
        return ApiResponse.success(orderService.cancel(id, request));
    }

    /**
     * 记录收款
     */
    @PostMapping("/{id}/payment")
    public ApiResponse<BookingOrder> recordPayment(@PathVariable Long id, @Valid @RequestBody OrderPaymentRequest request) {
        return ApiResponse.success(orderService.recordPayment(id, request));
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.success(null);
    }
}
