package com.yupi.oj.constant;

public class RedisKey {
    /**
     * 公共前缀
     */
    public static final String COMMON_PREFIX = "oj_api:";

    /**
     * 注册验证码: :邮箱
     */
    public static final String CODE_REGISTER = COMMON_PREFIX + "code:" + "code_register:";
    /**
     * 重置sk验证码: :邮箱
     */
    public static final String CODE_RESER_SK = COMMON_PREFIX + "code:" + "code_reset_sk:";
    /**
     * 邮箱登录验证码: :邮箱
     */
    public static final String CODE_EMAIL_LOGIN = COMMON_PREFIX + "code:" + "code_email_login:";
    /**
     * 重置密码验证码: :邮箱
     */
    public static final String CODE_RESET_PASSWORD_CODE = COMMON_PREFIX + "code:" + "code_reset_password_code:";

}
