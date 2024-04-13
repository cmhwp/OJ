package com.yupi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.model.dto.post.PostQueryRequest;
import com.yupi.oj.model.dto.postread.PostReadAddRequest;
import com.yupi.oj.model.dto.postread.PostReadQueryRequest;
import com.yupi.oj.model.entity.Post;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.PostVO;
import com.yupi.oj.service.PostReadService;
import com.yupi.oj.service.PostService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子浏览接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/post_read")
@Slf4j
public class PostReadController {

    @Resource
    private PostReadService postReadService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    /**
     * 浏览 / 取消浏览
     *
     * @param postReadAddRequest
     * @param request
     * @return resultNum 浏览变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doPostRead(@RequestBody PostReadAddRequest postReadAddRequest,
            HttpServletRequest request) {
        if (postReadAddRequest == null || postReadAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = postReadAddRequest.getPostId();
        int result = postReadService.doPostRead(postId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我浏览的帖子列表
     *
     * @param postQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PostVO>> listMyReadPostByPage(@RequestBody PostQueryRequest postQueryRequest,
            HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postReadService.listReadPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 获取用户浏览的帖子列表
     *
     * @param postReadQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostVO>> listReadPostByPage(@RequestBody PostReadQueryRequest postReadQueryRequest,
            HttpServletRequest request) {
        if (postReadQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postReadQueryRequest.getCurrent();
        long size = postReadQueryRequest.getPageSize();
        Long userId = postReadQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postReadService.listReadPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postReadQueryRequest.getPostQueryRequest()), userId);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }
}
