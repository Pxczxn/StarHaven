package com.xingqi.security;

/**
 * 用户上下文
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     */
    public static void setUser(LoginUser user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getUserId() {
        LoginUser user = getUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getRole() {
        LoginUser user = getUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * 清除当前登录用户
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
