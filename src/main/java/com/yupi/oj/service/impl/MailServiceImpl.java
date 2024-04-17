package com.yupi.oj.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.constant.RedisKey;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    private static final long REGISTER_CODE_EXPIRE = 5L;
    private static final long EMAIL_LOGIN_CODE_EXPIRE = 5L;
    private static final long RESET_PASSWORD_CODE_EXPIRE = 5L;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private void sendVerificationCode(String email, String keySuffix, long expireMinutes) {
        String redisKey = RedisKey.COMMON_PREFIX + email;
        Boolean isExists = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(isExists)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送验证码过于频繁，请稍后再试。");
        }
        String code = RandomUtil.randomNumbers(6);
        sendCodeEmail(email, code, keySuffix.replace("_CODE", ""), expireMinutes);
        redisTemplate.opsForValue().set(redisKey, code, expireMinutes, TimeUnit.MINUTES);
    }

    @Override
    public void sendRegisterCode(String email) {
        sendVerificationCode(email, "CODE_REGISTER", REGISTER_CODE_EXPIRE);
    }

    @Override
    public void sendEmailLoginCode(String email) {
        sendVerificationCode(email, "CODE_EMAIL_LOGIN", EMAIL_LOGIN_CODE_EXPIRE);
    }

    @Override
    public void sendResetPasswordCode(String email) {
        sendVerificationCode(email, "CODE_RESET_PASSWORD", RESET_PASSWORD_CODE_EXPIRE);
    }

    public String getCodeFromRedis(String email) {
        // 从 Redis 中获取指定邮箱的验证码
        return redisTemplate.opsForValue().get(RedisKey.COMMON_PREFIX + email);
    }
    @Override
    public boolean verifyCode(String email, String code) {
        // 从 Redis 中获取存储的验证码
        String storedCode = getCodeFromRedis(email);

        // 如果没有找到验证码，说明验证码已过期或不存在
        if (storedCode == null) {
            return false;
        }

        // 检查用户输入的验证码是否与存储的验证码匹配
        return storedCode.equals(code);
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(RedisKey.COMMON_PREFIX + email);
    }

    private void sendCodeEmail(String email, String code, String operation, long expire) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(operation);
        message.setText("您正在进行" + operation + "，您的验证码为：" + code + "，请在" + expire + "分钟内完成操作。");
        mailSender.send(message);
    }
}
