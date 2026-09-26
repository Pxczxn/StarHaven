package top.pxczxn.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String role;
    private java.util.List<String> roles;
    private Integer gender;
    private Integer orderCount;
    private Integer favoriteCount;
    private Integer couponCount;
}
