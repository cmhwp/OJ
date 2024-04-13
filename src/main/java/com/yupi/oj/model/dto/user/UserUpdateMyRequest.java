package com.yupi.oj.model.dto.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * GigHub
     */
    private String gitHubName;


    /**
     * 个人网站、博客或者作品集等
     */
    private String websites;


    /**
     * 性别 0为女性 1为男性
     */
    private Integer gender;

    /**
     * 地址
     */
    private String address;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 就读学校
     */
    private String school;

    /**
     * 公司
     */
    private String company;

    /**
     * 职位
     */
    private String position;


    @Serial
    private static final long serialVersionUID = 1L;
}