package com.yupi.oj.model.dto.follow;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子收藏 / 取消收藏请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class FollowAddRequest implements Serializable {

    /**
     * 被关注 id
     */
    private Long followId;

    private static final long serialVersionUID = 1L;
}