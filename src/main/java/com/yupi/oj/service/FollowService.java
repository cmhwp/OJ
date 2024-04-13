package com.yupi.oj.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.entity.Follow;
import com.yupi.oj.model.entity.User;

/**
* @author SEU
* @description 针对表【follow(题目收藏)】的数据库操作Service
* @createDate 2024-04-13 14:11:30
*/
public interface FollowService extends IService<Follow> {

    /**
     * 关注
     * @param followId 被关注用户id
     * @param loginUser
     */
    int doFollow(long followId, User loginUser);


    /**
     * 分页获取用户关注列表
     * @param page
     * @param queryWrapper
     * @param followUserId
     */
    Page<User> listFollowByPage(IPage<User> page, Wrapper<User> queryWrapper, long followUserId);


    /**
     * 关注（内部服务）
     *
     * @param userId
     * @param followId
     * @return
     */
    int doFollowInner(long userId, long followId);

    /**
     * @param followId
     * @param userId
     * @return
     **/
    long getFollowStatus(long followId, long userId);
}
