package com.yupi.oj.model.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.yupi.oj.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户视图（脱敏）
 */
@Data
public class UserVO implements Serializable {

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
     * 用户简介
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
     * 标签 JSON
     */
    private String tags;

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

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 关注数
     */
    private Integer concernNum;
    /**
     * 粉丝数
     */
    private Integer fansNum;

    /**
     * 是否关注
     */
    private Boolean isConcern;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 对象转封装类
     */
    public static UserVO objToVo(User user){
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    private static final long serialVersionUID = 1L;
}