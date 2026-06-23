package com.xingqi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.ApiResponse;
import com.xingqi.common.PageResponse;
import com.xingqi.entity.BookingOrder;
import com.xingqi.entity.Customer;
import com.xingqi.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询客户
     */
    @GetMapping
    public ApiResponse<PageResponse<Customer>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Customer> result = customerService.page(page, pageSize, keyword);
        return ApiResponse.success(PageResponse.of(result));
    }

    /**
     * 根据 ID 查询客户
     */
    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        return ApiResponse.success(customerService.getById(id));
    }

    /**
     * 新增客户
     */
    @PostMapping
    public ApiResponse<Customer> create(@Valid @RequestBody Customer customer) {
        return ApiResponse.success(customerService.create(customer));
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        return ApiResponse.success(customerService.update(id, customer));
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
     * 查询客户的历史订单
     */
    @GetMapping("/{id}/orders")
    public ApiResponse<List<BookingOrder>> getOrders(@PathVariable Long id) {
        return ApiResponse.success(customerService.getOrdersByCustomerId(id));
    }
}
