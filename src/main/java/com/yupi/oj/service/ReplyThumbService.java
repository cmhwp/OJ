package com.yupi.oj.service;

import com.yupi.oj.model.entity.ReplyThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【reply_thumb(帖子点赞)】的数据库操作Service
* @createDate 2024-04-01 13:57:37
*/
public interface ReplyThumbService extends IService<ReplyThumb> {
    /**
     * 点赞
     *
     * @param replyId
     * @param loginUser
     * @return
     */
    int doReplyThumb(long replyId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param replyId
     * @return
     */
    int doReplyThumbInner(long userId, long replyId);

    long getReply_thumbStatus(long replyId, long userId);
}
