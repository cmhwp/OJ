/*
我已经黑转粉了，我是正规军
                                ⠀⠀⠀ ⠀⠰⢷⢿⠄
                                ⠀⠀⠀⠀ ⠀⣼⣷⣄
                                ⠀ ⠀⣤⣿⣇⣿⣿⣧⣿⡄
                                ⢴⠾⠋⠀⠀⠻⣿⣷⣿⣿⡀
                                🏀    ⢀⣿⣿⡿⢿⠈⣿
                                ⠀⠀⠀ ⢠⣿⡿⠁⠀⡊⠀⠙
                                ⠀ ⠀⠀⢿⣿⠀⠀⠹⣿
                                ⠀⠀ ⠀⠀⠹⣷⡀⠀⣿⡄
🐔作者：芥末喂泡泡糖
*/
package com.yupi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.model.dto.postreply.PostReplyQueryRequest;
import com.yupi.oj.model.dto.replyfavour.ReplyFavourAddRequest;
import com.yupi.oj.model.dto.replyfavour.ReplyFavourQueryRequest;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.PostReplyVO;
import com.yupi.oj.service.PostReplyService;
import com.yupi.oj.service.ReplyFavourService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子回复收藏接口
 */
@RestController
@RequestMapping("/reply_favour")
@Slf4j
public class ReplyFavourController {

    @Resource
    private ReplyFavourService replyFavourService;

    @Resource
    private PostReplyService postReplyService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param replyFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doReplyFavour(@RequestBody ReplyFavourAddRequest replyFavourAddRequest,
                                               HttpServletRequest request) {
        if (replyFavourAddRequest == null || replyFavourAddRequest.getReplyId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long replyId = replyFavourAddRequest.getReplyId();
        int result = replyFavourService.doReplyFavour(replyId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postReplyQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PostReplyVO>> listMyFavourReplyByPage(@RequestBody PostReplyQueryRequest postReplyQueryRequest,
                                                                   HttpServletRequest request) {
        if (postReplyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = postReplyQueryRequest.getCurrent();
        long size = postReplyQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostReply> replyPage = replyFavourService.listFavourReplyByPage(new Page<>(current, size),
                postReplyService.getQueryWrapper(postReplyQueryRequest), loginUser.getId());
        return ResultUtils.success(postReplyService.getPostReplyVOPage(replyPage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param replyFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostReplyVO>> listFavourReplyByPage(@RequestBody ReplyFavourQueryRequest replyFavourQueryRequest,
                                                                 HttpServletRequest request) {
        if (replyFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = replyFavourQueryRequest.getCurrent();
        long size = replyFavourQueryRequest.getPageSize();
        Long userId = replyFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<PostReply> replyPage = replyFavourService.listFavourReplyByPage(new Page<>(current, size),
                postReplyService.getQueryWrapper(replyFavourQueryRequest.getPostReplyQueryRequest()), userId);
        return ResultUtils.success(postReplyService.getPostReplyVOPage(replyPage, request));
    }

    /**
     * 获取收藏状态
     *
     * @param replyId
     * @param request
     **/
    @GetMapping("get/reply_favour/status")
    public BaseResponse<Long> getReply_favourStatus(long replyId, HttpServletRequest request) {
        log.info("获取帖子收藏状态：帖子ID：{}, HTTP请求信息：{}", replyId, request);
        if (replyId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        // 从数据库中查询信息
        Long reply_thumbStatus = replyFavourService.getReply_favourStatus(replyId, userId);
        return ResultUtils.success(reply_thumbStatus);
    }
}
