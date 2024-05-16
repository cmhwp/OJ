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
package com.yupi.oj.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletRequest;

/**
 * 数据源接口（新接入的数据源必须实现）
 *
 * @param <T>
 */
public interface DataSource<T> {

    /**
     * 搜索接口
     *
     * @param searchText
     * @param current
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long current, long pageSize, HttpServletRequest request);
}
