package top.pxczxn.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter
                        .match("/api/v1/user/info", "/api/v1/user/logout", "/api/v1/user/refresh")
                        .match("/api/v1/favorite/**")
                        .match("/api/v1/order/**")
                        .match("/api/v1/pay/create", "/api/v1/pay/mockSuccess")
                        .match("/api/v1/comment/create")
                        .match("/api/v1/message/**")
                        .match("/api/v1/coupon/**")
                        .match("/api/v1/host/**")
                        .match("/api/v1/browse/**")
                        .match("/api/v1/search/history")
                        .match("/admin/**")
                        .check(r -> StpUtil.checkLogin())))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/houses/**",
                        "/banners/**"
                );
    }
}
