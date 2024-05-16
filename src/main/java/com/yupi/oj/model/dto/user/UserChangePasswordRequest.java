package com.yupi.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserChangePasswordRequest implements Serializable {

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}
