package com.yupi.oj.service;

import com.yupi.oj.model.entity.QuestionThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【question_thumb(题目点赞)】的数据库操作Service
* @createDate 2024-03-09 15:03:55
*/
public interface QuestionThumbService extends IService<QuestionThumb> {
    /**
     * 点赞
     *
     * @param questionId
     * @param loginUser
     * @return
     */
    int doQuestionThumb(long questionId, User loginUser);

    /**
     * 题目点赞（内部服务）
     *
     * @param userId
     * @param questionId
     * @return
     */
    int doQuestionThumbInner(long userId, long questionId);

    /**
     * 获取点赞状态
     *
     * @param questionId
     * @param userId
     * @return long
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/20 14:43
     **/
    long getQuestion_thumbStatus(long questionId, long userId);

}
