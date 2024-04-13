package com.yupi.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.dto.postreply.PostReplyQueryRequest;
import com.yupi.oj.model.entity.PostReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.oj.model.vo.PostReplyVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author SEU
* @description 针对表【post_reply(帖子回复)】的数据库操作Service
* @createDate 2024-04-01 13:57:37
*/
public interface PostReplyService extends IService<PostReply> {
    /**
     * 校验
     *
     * @param postReply
     * @param add
     */
    void validPostReply(PostReply postReply, boolean add);

    Page<PostReplyVO> listPostReplyVOByPage(PostReplyQueryRequest postReplyQueryRequest, HttpServletRequest request);


    /**
     * 获取查询条件
     *
     * @param postReplyQueryRequest
     * @return
     */
    QueryWrapper<PostReply> getQueryWrapper(PostReplyQueryRequest postReplyQueryRequest);


    PostReplyVO getPostReplyVO(PostReply postReply, HttpServletRequest request);


    /**
     * 分页获取帖子封装
     *
     * @param replyPage
     * @param request
     * @return
     */
    Page<PostReplyVO> getPostReplyVOPage(Page<PostReply> replyPage, HttpServletRequest request);

}
