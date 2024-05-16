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
package com.yupi.oj.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.yupi.oj.model.entity.Picture;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 */
@Data
public class SearchVO implements Serializable {


    private Page<UserVO> userVOList;

    private Page<PostVO> postVOList;

    private Page<Picture> pictureList;

    private Page<QuestionVO> questionVOList;

    private Page<?> dataList;

    @Serial
    private static final long serialVersionUID = 1986332396397463398L;
}
