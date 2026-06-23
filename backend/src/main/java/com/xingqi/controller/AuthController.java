package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.dto.request.LoginRequest;
import com.xingqi.dto.response.LoginResponse;
import com.xingqi.security.UserContext;
import com.xingqi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<LoginResponse.UserInfo> profile() {
        Long userId = UserContext.getUserId();
        LoginResponse.UserInfo userInfo = authService.getCurrentUser(userId);
        return ApiResponse.success(userInfo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT 是无状态的，退出登录由前端删除 Token 即可
        return ApiResponse.success("退出登录成功", null);
    }
}
