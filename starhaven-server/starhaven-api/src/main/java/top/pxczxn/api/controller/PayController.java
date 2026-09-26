package top.pxczxn.api.controller;

import top.pxczxn.business.dto.CreatePayDTO;
import top.pxczxn.business.service.PaymentService;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "支付")
@RestController
@RequestMapping("/api/v1/pay")
@RequiredArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    @Operation(summary = "创建支付单")
    @PostMapping("/create")
    public Result<Map<String, Object>> create(@Valid @RequestBody CreatePayDTO dto) {
        return Result.ok(paymentService.create(dto));
    }

    @Operation(summary = "支付回调（支付渠道通知）")
    @PostMapping("/callback")
    public Result<Void> callback(@RequestParam String paymentNo) {
        paymentService.callback(paymentNo);
        return Result.ok();
    }

    @Operation(summary = "演示环境模拟支付成功")
    @PostMapping("/mockSuccess")
    public Result<Void> mockSuccess(@RequestParam Long orderId) {
        paymentService.mockSuccess(orderId);
        return Result.ok();
    }
}
