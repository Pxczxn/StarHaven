package top.pxczxn.system.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import top.pxczxn.system.entity.User;
import top.pxczxn.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, Long.parseLong(loginId.toString())));
        if (user == null || user.getRole() == null) {
            return List.of();
        }
        return List.of(user.getRole());
    }
}
