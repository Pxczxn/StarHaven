package top.pxczxn.common.auth;

import cn.dev33.satoken.stp.StpUtil;

public final class StaffAuth {

    private StaffAuth() {
    }

    public static boolean admin() {
        return StpUtil.hasRole("ADMIN");
    }

    /** 商家只看自己的数据；平台管理员不过滤。 */
    public static Long merchantScopeId() {
        return admin() ? null : StpUtil.getLoginIdAsLong();
    }
}
