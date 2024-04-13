package com.yupi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.ReplyFavour;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.service.PostReplyService;
import com.yupi.oj.service.ReplyFavourService;
import com.yupi.oj.mapper.ReplyFavourMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author SEU
* @description 针对表【reply_favour(帖子收藏)】的数据库操作Service实现
* @createDate 2024-04-01 13:57:37
*/
@Service
public class ReplyFavourServiceImpl extends ServiceImpl<ReplyFavourMapper, ReplyFavour>
    implements ReplyFavourService{
    @Resource
    private PostReplyService postReplyService;

    @Resource
    private ReplyFavourMapper replyFavourMapper;

    /**
     * 帖子收藏
     *
     * @param replyId
     * @param loginUser
     * @return
     */
    @Override
    public int doReplyFavour(long replyId, User loginUser) {
        // 判断是否存在
        PostReply reply = postReplyService.getById(replyId);
        if (reply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        ReplyFavourService replyFavourService = (ReplyFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return replyFavourService.doReplyFavourInner(userId, replyId);
        }
    }

    @Override
    public Page<PostReply> listFavourReplyByPage(IPage<PostReply> page, Wrapper<PostReply> queryWrapper, long favourUserId) {
        if (favourUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listFavourReplyByPage(page, queryWrapper, favourUserId);
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param replyId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doReplyFavourInner(long userId, long replyId) {
        ReplyFavour replyFavour = new ReplyFavour();
        replyFavour.setUserId(userId);
        replyFavour.setReplyId(replyId);
        QueryWrapper<ReplyFavour> replyFavourQueryWrapper = new QueryWrapper<>(replyFavour);
        ReplyFavour oldReplyFavour = this.getOne(replyFavourQueryWrapper);
        boolean result;
        // 已收藏
        if (oldReplyFavour != null) {
            result = this.remove(replyFavourQueryWrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = postReplyService.update()
                        .eq("id", replyId)
                        .gt("favourNum", 0)
                        .setSql("favourNum = favourNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未帖子收藏
            result = this.save(replyFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = postReplyService.update()
                        .eq("id", replyId)
                        .setSql("favourNum = favourNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    @Override
    public long getReply_favourStatus(long replyId, long userId) {
        QueryWrapper<ReplyFavour> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("replyId", replyId);
        // 如果记录数为 0，则返回 -1，否则返回 1
        return replyFavourMapper.selectCount(queryWrapper) > 0 ? 1 : -1;
    }

}




