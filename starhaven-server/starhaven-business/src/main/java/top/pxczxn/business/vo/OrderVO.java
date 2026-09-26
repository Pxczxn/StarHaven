package top.pxczxn.business.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long houseId;
    private String houseTitle;
    private String houseCover;
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
}
