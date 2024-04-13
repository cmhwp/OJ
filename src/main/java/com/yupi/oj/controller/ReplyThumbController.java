package com.yupi.oj.controller;

import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.dto.replythumb.ReplyThumbAddRequest;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.service.ReplyThumbService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子回复点赞接口
 */
@RestController
@RequestMapping("/reply_thumb")
@Slf4j
public class ReplyThumbController {

    @Resource
    private ReplyThumbService replyThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param replyThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doReplyThumb(@RequestBody ReplyThumbAddRequest replyThumbAddRequest,
                                              HttpServletRequest request) {

        if (replyThumbAddRequest == null || replyThumbAddRequest.getReplyId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long replyId = replyThumbAddRequest.getReplyId();
        int result = replyThumbService.doReplyThumb(replyId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取点赞状态
     *
     * @param replyId
     * @param request
     **/
    @GetMapping("get/reply_thumb/status")
    public BaseResponse<Long> getReply_thumbStatus(long replyId, HttpServletRequest request) {
        if (replyId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        // 从数据库中查询信息
        Long reply_thumbStatus = replyThumbService.getReply_thumbStatus(replyId, userId);
        return ResultUtils.success(reply_thumbStatus);
    }

}
