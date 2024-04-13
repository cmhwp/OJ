package com.yupi.oj.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionFavour;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【question_favour(题目收藏)】的数据库操作Service
* @createDate 2024-03-09 15:03:38
*/
public interface QuestionFavourService extends IService<QuestionFavour> {

    /**
     * 帖子收藏
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    int doQuestionFavour(long questionId, User loginUser);

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper,
                                            long favourUserId);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param questionId
     * @return
     */
    int doQuestionFavourInner(long userId, long questionId);

    long getQuestion_favourStatus(long questionId, long userId);

}

