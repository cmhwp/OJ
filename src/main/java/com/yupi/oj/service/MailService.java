package com.yupi.oj.service;

public interface MailService {
    /**
     * 发送注册验证码
     *
     * @param email
     */
    void sendRegisterCode(String email);

    /**
     * 发送邮箱登录验证码
     */
    void sendEmailLoginCode(String email);

    /**
     * 发送重置密码验证码
     *
     * @param email
     */
    void sendResetPasswordCode(String email);

    /**
     * 验证
     */
    boolean verifyCode(String email, String code);

    // 删除存储在 Redis 中的验证码
    void deleteCode(String email);
}
