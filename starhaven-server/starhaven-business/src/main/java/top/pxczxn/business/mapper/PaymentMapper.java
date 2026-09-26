package top.pxczxn.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.pxczxn.business.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
