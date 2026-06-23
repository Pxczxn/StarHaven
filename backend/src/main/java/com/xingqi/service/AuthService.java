package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingqi.common.BusinessException;
import com.xingqi.dto.request.LoginRequest;
import com.xingqi.dto.response.LoginResponse;
import com.xingqi.entity.User;
import com.xingqi.mapper.UserMapper;
import com.xingqi.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw BusinessException.badRequest("用户名或密码错误");
        }

        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            throw BusinessException.forbidden("账号已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("用户名或密码错误");
        }

        // 检查角色：租客不允许登录后台
        if ("tenant".equals(user.getRole())) {
            throw BusinessException.forbidden("租客账号不允许登录后台管理系统");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构造响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole()
        );

        log.info("用户 {} 登录成功，角色：{}", user.getUsername(), user.getRole());

        return new LoginResponse(token, userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public LoginResponse.UserInfo getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }

        return new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole()
        );
    }
}
