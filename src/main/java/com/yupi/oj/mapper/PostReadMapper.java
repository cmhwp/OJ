package com.yupi.oj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.Post;
import com.yupi.oj.model.entity.PostRead;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author SEU
* @description 针对表【post_read(帖子点赞)】的数据库操作Mapper
* @createDate 2024-04-01 13:57:37
* @Entity com.yupi.oj.model.entity.PostRead
*/
public interface PostReadMapper extends BaseMapper<PostRead> {
    /**
     * 分页查询浏览帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param readUserId
     * @return
     */
    Page<Post> listReadPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper,
                                    long readUserId);
}




