package com.yupi.oj.model.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class UserResetPasswordRequest implements Serializable {
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误")
    private String email;
    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度为6-20")
    private String userPassword;
    /**
     * 验证码
     */
    @NotBlank
    private String code;
}
