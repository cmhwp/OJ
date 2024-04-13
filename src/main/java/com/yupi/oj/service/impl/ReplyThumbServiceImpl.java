package com.yupi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.ReplyThumb;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.service.PostReplyService;
import com.yupi.oj.service.ReplyThumbService;
import com.yupi.oj.mapper.ReplyThumbMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author SEU
* @description 针对表【reply_thumb(帖子点赞)】的数据库操作Service实现
* @createDate 2024-04-01 13:57:37
*/
@Service
public class ReplyThumbServiceImpl extends ServiceImpl<ReplyThumbMapper, ReplyThumb>
    implements ReplyThumbService{
    @Resource
    private PostReplyService postReplyService;

    @Resource
    private ReplyThumbMapper replyThumbMapper;

    /**
     * 点赞
     *
     * @param replyId
     * @param loginUser
     * @return
     */
    @Override
    public int doReplyThumb(long replyId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        PostReply postReply = postReplyService.getById(replyId);
        if (postReply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        ReplyThumbService replyThumbService = (ReplyThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return replyThumbService.doReplyThumbInner(userId, replyId);
        }
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
    public int doReplyThumbInner(long userId, long replyId) {
        ReplyThumb replyThumb = new ReplyThumb();
        replyThumb.setUserId(userId);
        replyThumb.setReplyId(replyId);
        QueryWrapper<ReplyThumb> thumbQueryWrapper = new QueryWrapper<>(replyThumb);
        ReplyThumb oldReplyThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldReplyThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 - 1
                result = postReplyService.update()
                        .eq("id", replyId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(replyThumb);
            if (result) {
                // 点赞数 + 1
                result = postReplyService.update()
                        .eq("id", replyId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }

    @Override
    public long getReply_thumbStatus(long replyId, long userId) {
        QueryWrapper<ReplyThumb> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("replyId", replyId);
        // 如果记录数为 0，则返回 -1，否则返回 1
        return replyThumbMapper.selectCount(queryWrapper) > 0 ? 1 : -1;
    }
}




