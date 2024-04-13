/*
我已经黑转粉了，我是正规军
                                ⠀⠀⠀ ⠀⠰⢷⢿⠄
                                ⠀⠀⠀⠀ ⠀⣼⣷⣄
                                ⠀ ⠀⣤⣿⣇⣿⣿⣧⣿⡄
                                ⢴⠾⠋⠀⠀⠻⣿⣷⣿⣿⡀
                                🏀    ⢀⣿⣿⡿⢿⠈⣿
                                ⠀⠀⠀ ⢠⣿⡿⠁⠀⡊⠀⠙
                                ⠀ ⠀⠀⢿⣿⠀⠀⠹⣿
                                ⠀⠀ ⠀⠀⠹⣷⡀⠀⣿⡄
🐔作者：芥末喂泡泡糖
*/
package com.yupi.oj.model.dto.postreply;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class PostReplyAddRequest implements Serializable {


    /**
     * 内容
     */
    private String content;

    /**
     * 帖子 id
     */
    private Long postId;

    /**
     * 父级回复 id
     */
    private Long parentReplyId;


    @Serial
    private static final long serialVersionUID = 1L;
}