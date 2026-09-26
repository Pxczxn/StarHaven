package top.pxczxn.api.controller;

import top.pxczxn.business.service.CouponBannerService;
import top.pxczxn.business.service.HostService;
import top.pxczxn.business.service.MessageService;
import top.pxczxn.business.vo.BannerVO;
import top.pxczxn.business.vo.CouponVO;
import top.pxczxn.business.vo.HouseCardVO;
import top.pxczxn.business.vo.MessageVO;
import top.pxczxn.business.vo.OrderVO;
import top.pxczxn.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "消息 / Banner / 优惠券 / 房东")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MiscController {

    private final MessageService messageService;
    private final CouponBannerService couponBannerService;
    private final HostService hostService;

    @Operation(summary = "首页 Banner")
    @GetMapping("/banner/list")
    public Result<List<BannerVO>> banners() {
        return Result.ok(couponBannerService.banners());
    }

    @Operation(summary = "我的消息")
    @GetMapping("/message/list")
    public Result<List<MessageVO>> messages() {
        return Result.ok(messageService.list());
    }

    @Operation(summary = "标记已读")
    @PutMapping("/message/read")
    public Result<Void> read(@RequestParam Long id) {
        messageService.read(id);
        return Result.ok();
    }

    @Operation(summary = "我的优惠券")
    @GetMapping("/coupon/my")
    public Result<List<CouponVO>> coupons() {
        return Result.ok(couponBannerService.myCoupons());
    }

    @Operation(summary = "申请成为房东")
    @PostMapping("/host/apply")
    public Result<Void> apply(@RequestParam String realName, @RequestParam(required = false) String idCard) {
        hostService.apply(realName, idCard);
        return Result.ok();
    }

    @Operation(summary = "房东房源")
    @GetMapping("/host/house")
    public Result<List<HouseCardVO>> hostHouses() {
        return Result.ok(hostService.myHouses());
    }

    @Operation(summary = "房东订单")
    @GetMapping("/host/order")
    public Result<List<OrderVO>> hostOrders() {
        return Result.ok(hostService.myOrders());
    }

    @Operation(summary = "房东收益")
    @GetMapping("/host/income")
    public Result<Map<String, Object>> income() {
        return Result.ok(hostService.income());
    }
}
