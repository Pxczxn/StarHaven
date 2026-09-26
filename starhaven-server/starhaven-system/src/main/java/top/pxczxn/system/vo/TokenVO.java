package top.pxczxn.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TokenVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "有效期（秒）")
    private long expiresIn;
}
