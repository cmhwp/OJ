package com.yupi.oj.model.dto.follow;

import com.yupi.oj.common.PageRequest;
import com.yupi.oj.model.dto.user.UserQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 关注查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FollowQueryRequest extends PageRequest implements Serializable {

    /**
     * 关注查询请求
     */
    private UserQueryRequest userQueryRequest;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}