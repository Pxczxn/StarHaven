package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.entity.BookingOrder;
import com.xingqi.dto.request.PaymentRequest;
import com.xingqi.service.BookingOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    @GetMapping
    public ApiResponse<PageResponse<BookingOrder>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        Page<BookingOrder> result = bookingOrderService.page(page, pageSize, keyword, status, startDate, endDate);
        return ApiResponse.success(PageResponse.of(result));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingOrder> getById(@PathVariable Long id) {
        return ApiResponse.success(bookingOrderService.getById(id));
    }

    @PostMapping
    public ApiResponse<BookingOrder> create(@Valid @RequestBody BookingOrder order) {
        return ApiResponse.success(bookingOrderService.create(order));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookingOrder> update(@PathVariable Long id, @Valid @RequestBody BookingOrder order) {
        return ApiResponse.success(bookingOrderService.update(id, order));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bookingOrderService.delete(id);
        return ApiResponse.success("订单已删除", null);
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Long id) {
        bookingOrderService.confirm(id);
        return ApiResponse.success("订单已确认", null);
    }

    @PutMapping("/{id}/check-in")
    public ApiResponse<Void> checkIn(@PathVariable Long id) {
        bookingOrderService.checkIn(id);
        return ApiResponse.success("已办理入住", null);
    }

    @PutMapping("/{id}/check-out")
    public ApiResponse<Void> checkOut(@PathVariable Long id) {
        bookingOrderService.checkOut(id);
        return ApiResponse.success("已办理退房", null);
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request
    ) {
        String cancelReason = request == null ? null : request.get("cancelReason");
        bookingOrderService.cancel(id, cancelReason);
        return ApiResponse.success("订单已取消", null);
    }

    @PutMapping("/{id}/payment")
    public ApiResponse<Void> payment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request
    ) {
        bookingOrderService.payment(id, request.getAmount(), request.getPaymentMethod());
        return ApiResponse.success("收款成功", null);
    }
}
