package top.pxczxn.business.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponVO {
    private Long id;
    private Long couponUserId;
    private String name;
    private BigDecimal discount;
    private BigDecimal conditionAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer used;
}
