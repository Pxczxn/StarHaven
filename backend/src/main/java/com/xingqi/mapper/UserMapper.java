package com.xingqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingqi.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
