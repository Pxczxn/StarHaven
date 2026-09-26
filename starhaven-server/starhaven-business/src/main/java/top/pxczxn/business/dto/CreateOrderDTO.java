package top.pxczxn.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateOrderDTO {
    @NotNull(message = "房源不能为空")
    private Long houseId;
    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;
    @NotNull(message = "离店日期不能为空")
    private LocalDate checkOutDate;
    @NotNull(message = "入住人数不能为空")
    private Integer guestCount;
    private Integer roomCount = 1;
    private String contactName;
    private String contactPhone;
    @Schema(description = "优惠券ID")
    private Long couponUserId;
}
