package com.yupi.oj.controller;

import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.service.MailService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/code")
@Slf4j
public class CodeController {
    @Resource
    private MailService mailService;

    @Resource
    private UserService userService;


    @GetMapping("/sendRegisterCode")
    public BaseResponse<String> sendRegisterCode(@RequestParam String email, HttpServletRequest request) {
        mailService.sendRegisterCode(email);
        return ResultUtils.success("注册码已发送到邮箱"); // 传递具体的成功信息
    }

    @GetMapping("/sendEmailLoginCode")
    public BaseResponse<String> sendEmailLoginCode(@RequestParam @Validated
                                           @Email(message = "邮箱格式错误") String email) {
        mailService.sendEmailLoginCode(email);
        return ResultUtils.success("邮箱验证码已发送到邮箱");
    }

    @GetMapping("/sendResetPasswordCode")
    public BaseResponse<String> sendResetPasswordCode(@RequestParam @Validated
                                              @Email(message = "邮箱格式错误") String email) {
        mailService.sendResetPasswordCode(email);
        return ResultUtils.success("重置密码已发送到邮箱");
    }
}

