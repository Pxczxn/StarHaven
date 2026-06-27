package com.xingqi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantAuthResponse {

    private String token;

    private TenantUserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantUserInfo {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private String idNumber;
        private String role;
    }
}
