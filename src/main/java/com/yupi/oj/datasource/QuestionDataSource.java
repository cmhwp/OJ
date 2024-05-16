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

import com.yupi.oj.model.dto.question.QuestionQueryRequest;
import com.yupi.oj.model.vo.QuestionVO;
import com.yupi.oj.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class QuestionDataSource implements DataSource<QuestionVO> {

    @Resource
    private QuestionService questionService;

    @Override
    public Page<QuestionVO> doSearch(String searchText, long current, long pageSize, HttpServletRequest request) {
        QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
        questionQueryRequest.setCurrent((int) current);
        questionQueryRequest.setPageSize((int) pageSize);
        questionQueryRequest.setSearchText(searchText);
        return questionService.listQuestionVOByPage(questionQueryRequest, request);
    }
}




