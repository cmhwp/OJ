package com.yupi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.model.dto.follow.FollowAddRequest;
import com.yupi.oj.model.dto.follow.FollowQueryRequest;
import com.yupi.oj.model.dto.user.UserQueryRequest;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.UserVO;
import com.yupi.oj.service.FollowService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 关注与粉丝
 */
@RestController
@RequestMapping("/follow")
@Slf4j
public class FollowController {

    @Resource
    private FollowService followService;

    @Resource
    private UserService userService;


    /**
     * 关注与取消关注
     */
    @PostMapping("/")
    public BaseResponse<Integer> doFollow(@RequestBody FollowAddRequest followAddRequest, HttpServletRequest request) {
        if (followAddRequest == null || followAddRequest.getFollowId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long followId = followAddRequest.getFollowId();
        int result = followService.doFollow(followId,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取关注列表
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<UserVO>> listFollowByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request){
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = followService.listFollowByPage(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest), userQueryRequest.getUserId());
        return ResultUtils.success(userService.getUserVOPage(userPage,request));
    }
    /**
     * 获取关注状态
     */
    @GetMapping("get/follow/status")
    public BaseResponse<Long> getPost_followStatus (long followId, HttpServletRequest request){
        log.info("获取关注状态：被关注ID：{}, HTTP请求信息：{}",followId,request.getRequestURI());
        if(followId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        //从数据库中查询信息
        Long followStatus = followService.getFollowStatus(followId,userId);
        return ResultUtils.success(followStatus);
    }
}
