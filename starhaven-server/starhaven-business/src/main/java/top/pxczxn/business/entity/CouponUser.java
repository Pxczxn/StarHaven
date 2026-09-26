package top.pxczxn.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("coupon_user")
public class CouponUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long couponId;
    private Long userId;
    private Integer used;
    private LocalDateTime createTime;
}
