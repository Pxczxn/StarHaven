package top.pxczxn.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("booking_order")
public class BookingOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long houseId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;
    private Integer roomCount;
    private String contactName;
    private String contactPhone;
    private BigDecimal housePrice;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Integer paymentStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
