package com.yupi.oj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.PostReply;
import com.yupi.oj.model.entity.ReplyFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author SEU
* @description 针对表【reply_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2024-04-01 13:57:37
* @Entity com.yupi.oj.model.entity.ReplyFavour
*/
public interface ReplyFavourMapper extends BaseMapper<ReplyFavour> {
    Page<PostReply> listFavourReplyByPage(IPage<PostReply> page, Wrapper<PostReply> queryWrapper, long favourUserId);
}




