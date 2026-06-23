package com.xingqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingqi.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户 Mapper
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
