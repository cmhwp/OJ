package com.yupi.oj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.DeleteRequest;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.mapper.PostReplyMapper;
import com.yupi.oj.model.dto.postreply.PostReplyAddRequest;
import com.yupi.oj.model.dto.postreply.PostReplyQueryRequest;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.PostReplyVO;
import com.yupi.oj.service.PostReplyService;
import com.yupi.oj.service.PostService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName PostReplyController 帖子回复接口
 * @Description TODO
 * @Author LXY
 * @Date 2024/3/14 15:02
 * @Version 1.0
 */

@RestController
@RequestMapping("/post_reply")
@Slf4j
public class PostReplyController {

    @Resource
    private PostReplyService postReplyService;

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;


    @Resource
    private PostReplyMapper postReplyMapper;

    /**
     * 添加回复
     *
     * @param postReplyAddRequest
     * @param request
     * @return com.lxy.ikunoj.common.BaseResponse<java.lang.Long>
     * @author: LXY
     * @description: TODO
     * @date: 2024/3/14 14:17
     **/
    @PostMapping("/add")
    public BaseResponse<Long> addPostReply(@RequestBody PostReplyAddRequest postReplyAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(postReplyAddRequest == null, ErrorCode.PARAMS_ERROR);
        PostReply postReply = new PostReply();
        BeanUtils.copyProperties(postReplyAddRequest, postReply);
        postReplyService.validPostReply(postReply, true);
        User loginUser = userService.getLoginUser(request);
        postReply.setUserId(loginUser.getId());
        postReply.setFavourNum(0);
        postReply.setThumbNum(0);

        // 更新帖子回复数
        boolean update = postService.update()
                .eq("id", postReply.getPostId())
                .setSql("replyNum = replyNum + 1")
                .update();
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);


        //如果是回复的回复，更新父回复的回复数量
        if (postReply.getParentReplyId() != null) {
            boolean updateReply = postReplyService.update()
                    .eq("id", postReply.getParentReplyId())
                    .setSql("replyNum = replyNum + 1")
                    .update();
            ThrowUtils.throwIf(!updateReply, ErrorCode.OPERATION_ERROR);
        }

        boolean result = postReplyService.save(postReply);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostReplyId = postReply.getId();
        return ResultUtils.success(newPostReplyId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostReply(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long postId = deleteRequest.getId();
        // 判断是否存在
        QueryWrapper<PostReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", postId);
        List<PostReply> postReplies = postReplyMapper.selectList(queryWrapper);
        ThrowUtils.throwIf(postReplies == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean b = false;
        for (PostReply postReply : postReplies) {
            b = postReplyService.removeById(postReply.getId());
        }
        return ResultUtils.success(b);
    }


    /**
     * 分页获取列表（封装类）
     *
     * @param postReplyQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostReplyVO>> listPostReplyVOByPage(@RequestBody PostReplyQueryRequest postReplyQueryRequest,
                                                                 HttpServletRequest request) {

        Page<PostReplyVO> postReplyVOPage = postReplyService.listPostReplyVOByPage(postReplyQueryRequest, request);
        return ResultUtils.success(postReplyVOPage);
    }
}
