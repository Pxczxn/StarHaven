package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.entity.BookingOrder;
import com.xingqi.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(financeService.summary(startDate, endDate));
    }

    @GetMapping("/revenue")
    public ApiResponse<List<Map<String, Object>>> revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(financeService.revenue(startDate, endDate));
    }

    @GetMapping("/payment-methods")
    public ApiResponse<List<Map<String, Object>>> paymentMethods(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(financeService.paymentMethods(startDate, endDate));
    }

    @GetMapping("/unpaid-orders")
    public ApiResponse<List<BookingOrder>> unpaidOrders() {
        return ApiResponse.success(financeService.unpaidOrders());
    }

    @GetMapping("/refund-orders")
    public ApiResponse<List<BookingOrder>> refundOrders() {
        return ApiResponse.success(financeService.refundOrders());
    }

    @GetMapping("/export")
    public ApiResponse<List<BookingOrder>> export(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(financeService.exportData(startDate, endDate));
    }
}
