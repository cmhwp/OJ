package com.yupi.oj.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.Post;
import com.yupi.oj.model.entity.PostRead;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【post_read(帖子点赞)】的数据库操作Service
* @createDate 2024-04-01 13:57:37
*/
public interface PostReadService extends IService<PostRead> {
    /**
     * 帖子浏览
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostRead(long postId, User loginUser);

    /**
     * 分页获取用户浏览的帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param readUserId
     * @return
     */
    Page<Post> listReadPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
                                    long readUserId);

    /**
     * 帖子浏览（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostReadInner(long userId, long postId);

}
