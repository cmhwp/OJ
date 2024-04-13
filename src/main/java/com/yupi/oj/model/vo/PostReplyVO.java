package com.yupi.oj.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName PostReplyVO
 * @Description TODO
 * @Author LXY
 * @Date 2024/3/14 14:54
 * @Version 1.0
 */
@Data
public class PostReplyVO implements Serializable {


    private Long id;

    /**
     * 内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 回复数
     */
    private Integer replyNum;

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 父级回复 id
     */
    private Long parentReplyId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否已点赞
     */
    private Boolean hasThumb;

    /**
     * 是否已收藏
     */
    private Boolean hasFavour;

    /**
     * 创建人信息
     */
    private UserVO userVO;

    @Serial
    private static final long serialVersionUID = 5339377408164377768L;

}
