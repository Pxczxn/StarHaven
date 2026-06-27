package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        return ApiResponse.success(dashboardService.summary());
    }

    @GetMapping("/order-trend")
    public ApiResponse<List<Map<String, Object>>> orderTrend() {
        return ApiResponse.success(dashboardService.orderTrend());
    }

    @GetMapping("/revenue-trend")
    public ApiResponse<List<Map<String, Object>>> revenueTrend() {
        return ApiResponse.success(dashboardService.revenueTrend());
    }

    @GetMapping("/room-status")
    public ApiResponse<List<Map<String, Object>>> roomStatus() {
        return ApiResponse.success(dashboardService.roomStatus());
    }
}
