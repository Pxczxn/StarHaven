package top.pxczxn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.pxczxn.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
