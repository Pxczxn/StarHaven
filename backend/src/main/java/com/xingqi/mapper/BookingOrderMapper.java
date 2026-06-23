package com.xingqi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingqi.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {
}
