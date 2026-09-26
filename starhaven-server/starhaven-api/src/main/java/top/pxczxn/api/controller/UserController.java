package top.pxczxn.api.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.business.entity.BookingOrder;
import top.pxczxn.business.mapper.BookingOrderMapper;
import top.pxczxn.business.service.CouponBannerService;
import top.pxczxn.business.service.FavoriteService;
import top.pxczxn.common.result.Result;
import top.pxczxn.system.dto.LoginDTO;
import top.pxczxn.system.dto.RegisterDTO;
import top.pxczxn.system.dto.SmsCodeDTO;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.service.AuthService;
import top.pxczxn.system.vo.TokenVO;
import top.pxczxn.system.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final FavoriteService favoriteService;
    private final CouponBannerService couponBannerService;
    private final BookingOrderMapper bookingOrderMapper;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.ok();
    }

    @Operation(summary = "登录（账号密码 / 短信）")
    @PostMapping("/login")
    public Result<TokenVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(authService.login(dto));
    }

    @Operation(summary = "发送短信验证码（演示直接返回验证码）")
    @PostMapping("/sms")
    public Result<String> sms(@Valid @RequestBody SmsCodeDTO dto) {
        return Result.ok(authService.sendSms(dto.getPhone()));
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        User user = authService.requireLoginUser();
        UserInfoVO vo = authService.toUserInfo(user);
        vo.setFavoriteCount((int) favoriteService.countByUser(user.getId()));
        vo.setCouponCount((int) couponBannerService.couponCount(user.getId()));
        vo.setOrderCount(bookingOrderMapper.selectCount(
                Wrappers.<BookingOrder>lambdaQuery().eq(BookingOrder::getUserId, user.getId())).intValue());
        return Result.ok(vo);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public Result<TokenVO> refresh() {
        return Result.ok(authService.refresh());
    }
}
