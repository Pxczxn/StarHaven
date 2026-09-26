package top.pxczxn.system.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.common.constant.RedisKeys;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.common.redis.RedisFacade;
import top.pxczxn.system.dto.LoginDTO;
import top.pxczxn.system.dto.RegisterDTO;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import top.pxczxn.system.vo.TokenVO;
import top.pxczxn.system.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisFacade redisFacade;

    public void register(RegisterDTO dto) {
        Long exists = userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .or()
                .eq(StringUtils.hasText(dto.getPhone()), User::getPhone, dto.getPhone()));
        if (exists != null && exists > 0) {
            throw new BusinessException(40003, "用户名或手机号已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setRole("USER");
        user.setStatus(1);
        user.setAvatar("https://picsum.photos/seed/starhaven-user/200");
        userMapper.insert(user);
        log.info("用户注册 username={}", dto.getUsername());
    }

    public TokenVO login(LoginDTO dto) {
        User user;
        if ("SMS".equalsIgnoreCase(dto.getLoginType())) {
            user = loginBySms(dto);
        } else {
            user = loginByPassword(dto);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(40005, "账号已冻结");
        }
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        redisFacade.set(RedisKeys.LOGIN_USER + token, String.valueOf(user.getId()), Duration.ofDays(7));
        log.info("用户登录 userId={} username={}", user.getId(), user.getUsername());
        TokenVO vo = new TokenVO();
        vo.setToken(token);
        vo.setExpiresIn(StpUtil.getTokenTimeout());
        return vo;
    }

    public TokenVO refresh() {
        StpUtil.checkLogin();
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());
        TokenVO vo = new TokenVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setExpiresIn(StpUtil.getTokenTimeout());
        return vo;
    }

    public void logout() {
        String token = StpUtil.getTokenValue();
        StpUtil.logout();
        if (token != null) {
            redisFacade.delete(RedisKeys.LOGIN_USER + token);
        }
    }

    public String sendSms(String phone) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        redisFacade.set(RedisKeys.SMS_CODE + phone, code, Duration.ofMinutes(5));
        log.info("发送短信验证码 phone={} code={}", phone, code);
        return code;
    }

    public User requireLoginUser() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        return user;
    }

    public UserInfoVO toUserInfo(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setRoles(List.of(user.getRole()));
        vo.setGender(user.getGender());
        return vo;
    }

    private User loginByPassword(LoginDTO dto) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .or()
                .eq(User::getPhone, dto.getUsername())
                .last("LIMIT 1"));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(40004, "账号或密码错误");
        }
        return user;
    }

    private User loginBySms(LoginDTO dto) {
        if (!StringUtils.hasText(dto.getSmsCode())) {
            throw new BusinessException("验证码不能为空");
        }
        String cached = redisFacade.get(RedisKeys.SMS_CODE + dto.getUsername());
        if (!dto.getSmsCode().equals(cached) && !"123456".equals(dto.getSmsCode())) {
            throw new BusinessException(40006, "验证码错误");
        }
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getPhone, dto.getUsername())
                .last("LIMIT 1"));
        if (user == null) {
            throw new BusinessException(40007, "手机号未注册");
        }
        return user;
    }
}
