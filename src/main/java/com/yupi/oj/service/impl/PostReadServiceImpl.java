package com.yupi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.entity.Post;
import com.yupi.oj.model.entity.PostFavour;
import com.yupi.oj.model.entity.PostRead;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.service.PostFavourService;
import com.yupi.oj.service.PostReadService;
import com.yupi.oj.mapper.PostReadMapper;
import com.yupi.oj.service.PostService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author SEU
* @description 针对表【post_read(帖子点赞)】的数据库操作Service实现
* @createDate 2024-04-01 13:57:37
*/
@Service
public class PostReadServiceImpl extends ServiceImpl<PostReadMapper, PostRead>
    implements PostReadService{
    @Resource
    private PostService postService;

    /**
     * 帖子浏览
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doPostRead(long postId, User loginUser) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子浏览
        long userId = loginUser.getId();
        // 每个用户串行帖子浏览
        // 锁必须要包裹住事务方法
        PostReadService postReadService = (PostReadService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postReadService.doPostReadInner(userId, postId);
        }
    }

    @Override
    public Page<Post> listReadPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long readUserId) {
        if (readUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listReadPostByPage(page, queryWrapper, readUserId);
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostReadInner(long userId, long postId) {
        PostRead postRead = new PostRead();
        postRead.setUserId(userId);
        postRead.setPostId(postId);
        QueryWrapper<PostRead> postReadQueryWrapper = new QueryWrapper<>(postRead);
        PostRead oldPostRead = this.getOne(postReadQueryWrapper);
        // 如果用户已浏览过，则不做任何数据库操作，直接返回0（表示没有变化）
        if (oldPostRead != null) {
            return 0;
        } else {
            // 未浏览，则保存浏览记录
            boolean result = this.save(postRead);
            if (result) {
                // 并增加帖子的浏览数
                result = postService.update()
                        .eq("id", postId)
                        .setSql("readNum = readNum + 1")
                        .update();
                // 如果成功增加浏览量，则返回1，否则抛出异常
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return 1; // 表示浏览量增加了1
    }
}




