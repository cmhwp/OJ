package com.yupi.oj.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.ReplyFavour;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【reply_favour(帖子收藏)】的数据库操作Service
* @createDate 2024-04-01 13:57:37
*/
public interface ReplyFavourService extends IService<ReplyFavour> {
    /**
     * 帖子回复收藏
     *
     * @param replyId
     * @param loginUser
     * @return int
     * @author: LXY
     * @description:
     * @date: 2024/3/15 8:59
     **/

    int doReplyFavour(long replyId, User loginUser);


    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<PostReply> listFavourReplyByPage(IPage<PostReply> page, Wrapper<PostReply> queryWrapper,
                                          long favourUserId);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param replyId
     * @return
     */
    int doReplyFavourInner(long userId, long replyId);

    /**
     * @param replyId
     * @param userId
     * @return long
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/20 13:46
     **/
    long getReply_favourStatus(long replyId, long userId);

}
