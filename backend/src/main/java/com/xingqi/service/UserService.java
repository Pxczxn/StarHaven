package com.xingqi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingqi.common.BusinessException;
import com.xingqi.entity.User;
import com.xingqi.mapper.UserMapper;
import com.xingqi.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Page<User> page(Integer pageNum, Integer pageSize, String keyword, String role, String status) {
        ensureAdmin();
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (role != null && !role.isBlank()) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        Page<User> result = userMapper.selectPage(page, wrapper);
        result.getRecords().forEach(this::sanitize);
        return result;
    }

    public User getById(Long id) {
        ensureAdmin();
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("账号不存在");
        }
        return sanitize(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User create(User user) {
        ensureAdmin();
        validateUniqueUsername(user.getUsername(), null);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw BusinessException.badRequest("密码不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("landlord");
        }
        if (user.getStatus() == null || user.getStatus().isBlank()) {
            user.setStatus("enabled");
        }
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        log.info("创建账号成功: {}", user.getUsername());
        return sanitize(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User update(Long id, User user) {
        ensureAdmin();
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw BusinessException.notFound("账号不存在");
        }
        validateUniqueUsername(user.getUsername(), id);
        user.setId(id);
        user.setPassword(existing.getPassword());
        user.setCreatedAt(existing.getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return sanitize(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, String password) {
        ensureAdmin();
        if (password == null || password.length() < 6) {
            throw BusinessException.badRequest("密码至少 6 位");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("账号不存在");
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateStatus(Long id, String status) {
        ensureAdmin();
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("账号不存在");
        }
        if (id.equals(UserContext.getUserId()) && "disabled".equals(status)) {
            throw BusinessException.badRequest("不能禁用当前登录账号");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return sanitize(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ensureAdmin();
        if (id.equals(UserContext.getUserId())) {
            throw BusinessException.badRequest("不能删除当前登录账号");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.notFound("账号不存在");
        }
        userMapper.deleteById(id);
    }

    private void ensureAdmin() {
        if (!"admin".equals(UserContext.getRole())) {
            throw BusinessException.forbidden("只有管理员可以管理账号");
        }
    }

    private void validateUniqueUsername(String username, Long excludeId) {
        if (username == null || username.isBlank()) {
            throw BusinessException.badRequest("用户名不能为空");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (excludeId != null) {
            wrapper.ne(User::getId, excludeId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw BusinessException.badRequest("用户名已存在");
        }
    }

    private User sanitize(User user) {
        user.setPassword(null);
        return user;
    }
}
