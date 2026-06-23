package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.dto.request.CustomerRequest;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询客户
     */
    @GetMapping
    public ApiResponse<PageResponse<Customer>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword
    ) {
        PageResponse<Customer> result = customerService.list(page, pageSize, keyword);
        return ApiResponse.success(result);
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        return ApiResponse.success(customer);
    }

    /**
     * 创建客户
     */
    @PostMapping
    public ApiResponse<Customer> create(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.create(request);
        return ApiResponse.success("创建成功", customer);
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public ApiResponse<Customer> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request
    ) {
        Customer customer = customerService.update(id, request);
        return ApiResponse.success("更新成功", customer);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 获取客户的历史订单
     */
    @GetMapping("/{id}/orders")
    public ApiResponse<List<BookingOrder>> getOrders(@PathVariable Long id) {
        List<BookingOrder> orders = customerService.getOrders(id);
        return ApiResponse.success(orders);
    }
}
