package top.pxczxn.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @Schema(description = "用户名或手机号")
    @NotBlank(message = "账号不能为空")
    private String username;

    @Schema(description = "密码，短信登录可留空")
    private String password;

    @Schema(description = "登录方式 PASSWORD / SMS")
    private String loginType;

    @Schema(description = "短信验证码")
    private String smsCode;
}
