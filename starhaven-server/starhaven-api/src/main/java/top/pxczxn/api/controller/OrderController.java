package top.pxczxn.api.controller;

import top.pxczxn.business.dto.CreateOrderDTO;
import top.pxczxn.business.service.OrderService;
import top.pxczxn.business.vo.OrderVO;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单")
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderVO> create(@Valid @RequestBody CreateOrderDTO dto) {
        return Result.ok(orderService.create(dto));
    }

    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public Result<PageData<OrderVO>> my(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        return Result.ok(orderService.myOrders(status, page, size));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.detail(id));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestParam Long id) {
        orderService.cancel(id);
        return Result.ok();
    }
}
