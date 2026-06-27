package com.xingqi.controller;

import com.xingqi.common.ApiResponse;
import com.xingqi.dto.request.TenantLoginRequest;
import com.xingqi.dto.request.TenantRegisterRequest;
import com.xingqi.dto.request.TenantSmsCodeRequest;
import com.xingqi.dto.request.TenantVerifyRequest;
import com.xingqi.dto.response.TenantAuthResponse;
import com.xingqi.service.TenantAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/auth")
@RequiredArgsConstructor
public class TenantAuthController {

    private final TenantAuthService tenantAuthService;

    @PostMapping("/sms-code")
    public ApiResponse<String> sendSmsCode(@Valid @RequestBody TenantSmsCodeRequest request) {
        return ApiResponse.success(tenantAuthService.sendRegisterCode(request));
    }

    @PostMapping("/register")
    public ApiResponse<TenantAuthResponse> register(@Valid @RequestBody TenantRegisterRequest request) {
        return ApiResponse.success(tenantAuthService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TenantAuthResponse> login(@Valid @RequestBody TenantLoginRequest request) {
        return ApiResponse.success(tenantAuthService.login(request));
    }

    @GetMapping("/profile")
    public ApiResponse<TenantAuthResponse.TenantUserInfo> profile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return ApiResponse.success(tenantAuthService.profile(authorization));
    }

    @PutMapping("/verify")
    public ApiResponse<TenantAuthResponse.TenantUserInfo> verify(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody TenantVerifyRequest request
    ) {
        return ApiResponse.success(tenantAuthService.verify(authorization, request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
