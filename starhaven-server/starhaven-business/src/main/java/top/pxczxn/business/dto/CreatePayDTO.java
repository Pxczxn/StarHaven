package top.pxczxn.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePayDTO {
    @NotNull
    private Long orderId;
    @Schema(description = "WECHAT / ALIPAY")
    private String payType;
}
