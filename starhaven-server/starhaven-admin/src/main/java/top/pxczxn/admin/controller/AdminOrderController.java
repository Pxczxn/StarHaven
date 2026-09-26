package top.pxczxn.admin.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import top.pxczxn.business.service.HostService;
import top.pxczxn.business.service.OrderService;
import top.pxczxn.business.vo.OrderVO;
import top.pxczxn.common.result.PageData;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台订单与房东")
@SaCheckRole("ADMIN")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final HostService hostService;

    @Operation(summary = "订单分页")
    @GetMapping("/order/page")
    public Result<PageData<OrderVO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(orderService.adminPage(page, size, status, keyword));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/order/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.ok(orderService.adminDetail(id));
    }

    @Operation(summary = "修改订单状态")
    @PutMapping("/order/status")
    public Result<Void> status(@RequestParam Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "退款处理")
    @PutMapping("/order/refund")
    public Result<Void> refund(@RequestParam Long id) {
        orderService.refund(id);
        return Result.ok();
    }

    @Operation(summary = "审核房东认证")
    @PutMapping("/host/audit")
    public Result<Void> hostAudit(@RequestParam Long applyId, @RequestParam Integer status,
                                  @RequestParam(required = false) String remark) {
        hostService.auditHost(applyId, status, remark);
        return Result.ok();
    }
}
