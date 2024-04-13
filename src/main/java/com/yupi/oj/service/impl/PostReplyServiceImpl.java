package com.yupi.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.constant.CommonConstant;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.mapper.ReplyFavourMapper;
import com.yupi.oj.mapper.ReplyThumbMapper;
import com.yupi.oj.model.dto.postreply.PostReplyQueryRequest;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.ReplyFavour;
import com.yupi.oj.model.entity.ReplyThumb;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.PostReplyVO;
import com.yupi.oj.model.vo.UserVO;
import com.yupi.oj.service.PostReplyService;
import com.yupi.oj.mapper.PostReplyMapper;
import com.yupi.oj.service.UserService;
import com.yupi.oj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author SEU
* @description 针对表【post_reply(帖子回复)】的数据库操作Service实现
* @createDate 2024-04-01 13:57:37
*/
@Service
public class PostReplyServiceImpl extends ServiceImpl<PostReplyMapper, PostReply>
    implements PostReplyService{
    @Resource
    private UserService userService;

    @Resource
    private ReplyThumbMapper replyThumbMapper;


    @Resource
    private ReplyFavourMapper replyFavourMapper;

    @Override
    public void validPostReply(PostReply postReply, boolean add) {
        if (postReply == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = postReply.getContent();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(content) && SensitiveWordHelper.contains(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容含义敏感词");
        }

    }

    @Override
    public Page<PostReplyVO> listPostReplyVOByPage(PostReplyQueryRequest postReplyQueryRequest, HttpServletRequest request) {
        long current = postReplyQueryRequest.getCurrent();
        long size = postReplyQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<PostReply> postPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(postReplyQueryRequest));
        return this.getPostReplyVOPage(postPage, request);
    }


    /**
     * 获取查询包装类
     *
     * @param postReplyQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PostReply> getQueryWrapper(PostReplyQueryRequest postReplyQueryRequest) {
        QueryWrapper<PostReply> queryWrapper = new QueryWrapper<>();
        if (postReplyQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postReplyQueryRequest.getSearchText();
        String sortField = postReplyQueryRequest.getSortField();
        String sortOrder = postReplyQueryRequest.getSortOrder();
        Long id = postReplyQueryRequest.getId();
        String content = postReplyQueryRequest.getContent();
        Long userId = postReplyQueryRequest.getUserId();
        Long postId = postReplyQueryRequest.getPostId();
        Long parentReplyId = postReplyQueryRequest.getParentReplyId();
        Long notId = postReplyQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(postId), "postId", postId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(parentReplyId), "parentReplyId", parentReplyId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public PostReplyVO getPostReplyVO(PostReply postReply, HttpServletRequest request) {
        PostReplyVO postReplyVO = new PostReplyVO();
        BeanUtils.copyProperties(postReply, postReplyVO);
        long replyId = postReply.getId();
        // 1. 关联查询用户信息
        Long userId = postReply.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postReplyVO.setUserVO(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<ReplyThumb> replyThumbQueryWrapper = new QueryWrapper<>();
            replyThumbQueryWrapper.in("replyId", replyId);
            replyThumbQueryWrapper.eq("userId", loginUser.getId());
            ReplyThumb replyThumb = replyThumbMapper.selectOne(replyThumbQueryWrapper);
            postReplyVO.setHasThumb(replyThumb != null);
            // 获取收藏
            QueryWrapper<ReplyFavour> replyFavourQueryWrapper = new QueryWrapper<>();
            replyFavourQueryWrapper.in("replyId", replyId);
            replyFavourQueryWrapper.eq("userId", loginUser.getId());
            ReplyFavour replyFavour = replyFavourMapper.selectOne(replyFavourQueryWrapper);
            postReplyVO.setHasFavour(replyFavour != null);
        }
        return postReplyVO;
    }

    @Override
    public Page<PostReplyVO> getPostReplyVOPage(Page<PostReply> postReplyPage, HttpServletRequest request) {
        List<PostReply> postReplyList = postReplyPage.getRecords();
        Page<PostReplyVO> postReplyVOPage = new Page<>(postReplyPage.getCurrent(), postReplyPage.getSize(), postReplyPage.getTotal());
        if (CollectionUtils.isEmpty(postReplyList)) {
            return postReplyVOPage;
        }
        List<PostReplyVO> postReplyVOList = postReplyList.stream().map(postReply -> getPostReplyVO(postReply, request)).collect(Collectors.toList());
        postReplyVOPage.setRecords(postReplyVOList);
        return postReplyVOPage;
    }
}




