package com.yupi.oj.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginEmailRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;
}
