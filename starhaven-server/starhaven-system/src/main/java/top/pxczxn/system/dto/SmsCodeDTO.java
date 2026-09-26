package top.pxczxn.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsCodeDTO {

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
