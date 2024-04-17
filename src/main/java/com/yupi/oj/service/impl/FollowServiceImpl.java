package com.yupi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.entity.Follow;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.service.FollowService;
import com.yupi.oj.mapper.FollowMapper;
import com.yupi.oj.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author SEU
* @description 针对表【follow(题目收藏)】的数据库操作Service实现
* @createDate 2024-04-13 14:11:30
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{

    @Resource
    private UserService userService;

    @Resource
    private FollowMapper followMapper;

    /**
     * 关注
     */
    @Override
    public int doFollow(long followId, User loginUser) {
        //判断是否存在
        User user = userService.getById(followId);

        if(user == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //是否已关注
        long userId = loginUser.getId();
        // 每个用户串行关注
        // 锁必须要包裹住事务方法
        FollowService followService = (FollowService) AopContext.currentProxy();
        synchronized (String.valueOf(followId).intern()){
            return followService.doFollowInner(userId,followId);
        }
    }

    @Override
    public Page<User> listFollowByPage(IPage<User> page, Wrapper<User> queryWrapper, long followUserId) {
        if(followUserId <=0 ){
            return new Page<>();
        }
        return baseMapper.listFollowByPage(page, queryWrapper, followUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doFollowInner(long userId, long followId) {
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowId(followId);
        QueryWrapper<Follow> followQueryWrapper = new QueryWrapper<>(follow);
        Follow oldfollow = this.getOne(followQueryWrapper);
        boolean result;
        //已关注
        if (oldfollow != null) {
            result = this.remove(followQueryWrapper);
            if (result) {
                // 关注者的关注数-1
                userService.update()
                        .eq("id", userId)
                        .gt("concernNum", 0)
                        .setSql("concernNum = concernNum - 1")
                        .update();
                // 被关注者的粉丝数-1
                result = userService.update()
                        .eq("id", followId)
                        .gt("fansNum", 0)
                        .setSql("fansNum = fansNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            //未关注
            result = this.save(follow);
            if (result) {
                // 关注者的关注数+1
                userService.update()
                        .eq("id", userId)
                        .setSql("concernNum = concernNum + 1")
                        .update();
                // 被关注者的粉丝数+1
                result = userService.update()
                        .eq("id", followId)
                        .setSql("fansNum = fansNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }


    @Override
    public long getFollowStatus(long followId, long userId) {
        QueryWrapper<Follow> followQueryWrapper = new QueryWrapper<>();
        followQueryWrapper.eq("userId", userId)
                .eq("followId", followId);
        // 如果记录数为 0，则返回 -1，否则返回 1
        return followMapper.selectCount(followQueryWrapper) >0 ? 1 : -1;
    }

}




