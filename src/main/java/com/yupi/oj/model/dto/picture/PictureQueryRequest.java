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
package com.yupi.oj.model.dto.picture;

import com.yupi.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}