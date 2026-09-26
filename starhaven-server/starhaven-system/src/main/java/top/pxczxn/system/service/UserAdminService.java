package top.pxczxn.system.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.pxczxn.common.exception.BusinessException;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserMapper userMapper;

    public Page<User> page(long page, long size, String keyword, String role) {
        return userMapper.selectPage(Page.of(page, size), Wrappers.<User>lambdaQuery()
                .and(StringUtils.hasText(keyword), w -> w
                        .like(User::getUsername, keyword)
                        .or()
                        .like(User::getNickname, keyword)
                        .or()
                        .like(User::getPhone, keyword))
                .eq(StringUtils.hasText(role), User::getRole, role)
                .orderByDesc(User::getId));
    }

    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public void updateRole(Long id, String role) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
    }

    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}
