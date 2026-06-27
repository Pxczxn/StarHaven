package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingqi.common.BusinessException;
import com.xingqi.dto.request.TenantLoginRequest;
import com.xingqi.dto.request.TenantRegisterRequest;
import com.xingqi.dto.request.TenantSmsCodeRequest;
import com.xingqi.dto.request.TenantVerifyRequest;
import com.xingqi.dto.response.TenantAuthResponse;
import com.xingqi.entity.User;
import com.xingqi.mapper.UserMapper;
import com.xingqi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantAuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, SmsCodeRecord> registerCodes = new ConcurrentHashMap<>();

    public String sendRegisterCode(TenantSmsCodeRequest request) {
        if (existsByUsername(request.getPhone()) || existsTenantByPhone(request.getPhone())) {
            throw BusinessException.badRequest("该手机号已注册");
        }
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        registerCodes.put(request.getPhone(), new SmsCodeRecord(code, LocalDateTime.now().plusMinutes(5)));
        log.info("租客注册验证码: {} - {}", request.getPhone(), code);
        return code;
    }

    @Transactional(rollbackFor = Exception.class)
    public TenantAuthResponse register(TenantRegisterRequest request) {
        if (existsByUsername(request.getPhone()) || existsTenantByPhone(request.getPhone())) {
            throw BusinessException.badRequest("该手机号已注册");
        }
        verifyRegisterCode(request.getPhone(), request.getVerificationCode());

        User user = new User();
        user.setUsername(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        if (StringUtils.hasText(request.getIdNumber())) {
            user.setIdNumber(request.getIdNumber().toUpperCase());
        }
        user.setRole("tenant");
        user.setStatus("enabled");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);

        log.info("租客注册成功: {}", user.getPhone());
        return buildAuthResponse(user);
    }

    public TenantAuthResponse login(TenantLoginRequest request) {
        User user = findTenantByPhone(request.getPhone());
        if (user == null) {
            throw BusinessException.badRequest("手机号或密码错误");
        }
        if ("disabled".equals(user.getStatus())) {
            throw BusinessException.forbidden("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("手机号或密码错误");
        }
        return buildAuthResponse(user);
    }

    public TenantAuthResponse.TenantUserInfo profile(String authorization) {
        User user = getTenantByAuthorization(authorization);
        return toUserInfo(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public TenantAuthResponse.TenantUserInfo verify(String authorization, TenantVerifyRequest request) {
        User user = getTenantByAuthorization(authorization);
        user.setIdNumber(request.getIdNumber().toUpperCase());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return toUserInfo(user);
    }

    private void verifyRegisterCode(String phone, String verificationCode) {
        SmsCodeRecord record = registerCodes.get(phone);
        if (record == null) {
            throw BusinessException.badRequest("请先获取验证码");
        }
        if (record.expireAt().isBefore(LocalDateTime.now())) {
            registerCodes.remove(phone);
            throw BusinessException.badRequest("验证码已过期，请重新获取");
        }
        if (!record.code().equals(verificationCode)) {
            throw BusinessException.badRequest("验证码不正确");
        }
        registerCodes.remove(phone);
    }

    private User getTenantByAuthorization(String authorization) {
        String token = extractToken(authorization);
        if (!jwtUtil.validateToken(token)) {
            throw BusinessException.unauthorized("登录已过期，请重新登录");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user == null || !"tenant".equals(user.getRole())) {
            throw BusinessException.unauthorized("租客账号不存在");
        }
        if ("disabled".equals(user.getStatus())) {
            throw BusinessException.forbidden("账号已被禁用");
        }
        return user;
    }

    private TenantAuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new TenantAuthResponse(token, toUserInfo(user));
    }

    private TenantAuthResponse.TenantUserInfo toUserInfo(User user) {
        return new TenantAuthResponse.TenantUserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getIdNumber(),
                user.getRole()
        );
    }

    private User findTenantByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "tenant")
                .and(w -> w.eq(User::getUsername, phone).or().eq(User::getPhone, phone))
                .last("LIMIT 1");
        return userMapper.selectOne(wrapper);
    }

    private boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    private boolean existsTenantByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "tenant").eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw BusinessException.unauthorized("未登录或登录已过期");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }

    private record SmsCodeRecord(String code, LocalDateTime expireAt) {
    }
}
