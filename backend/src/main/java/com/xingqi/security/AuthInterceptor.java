package com.xingqi.security;

import com.xingqi.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跨域预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取请求路径
        String uri = request.getRequestURI();

        // public 接口放行
        if (uri.startsWith("/api/public/")) {
            return true;
        }

        // 登录接口放行
        if (uri.equals("/api/auth/login")) {
            return true;
        }

        if (uri.equals("/api/tenant/auth/login") || uri.equals("/api/tenant/auth/register")) {
            return true;
        }

        // 健康检查接口放行
        if (uri.equals("/api/health")) {
            return true;
        }

        // 获取 Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw BusinessException.unauthorized("未登录或登录已过期");
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw BusinessException.unauthorized("Token 无效或已过期");
        }

        // 解析用户信息并存入上下文
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            LoginUser loginUser = new LoginUser(userId, username, null, role);
            UserContext.setUser(loginUser);

            log.debug("用户 {} (ID: {}, Role: {}) 通过认证", username, userId, role);
        } catch (Exception e) {
            log.error("解析 Token 失败", e);
            throw BusinessException.unauthorized("Token 解析失败");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }
}
