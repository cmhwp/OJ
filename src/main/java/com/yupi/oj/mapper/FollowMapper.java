package com.yupi.oj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.Follow;
import com.yupi.oj.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
* @author SEU
* @description 针对表【follow(题目收藏)】的数据库操作Mapper
* @createDate 2024-04-13 14:11:30
* @Entity generator.domain.Follow
*/
public interface FollowMapper extends BaseMapper<Follow> {
    /**
     * 分页查询收藏帖子列表
     *
     */
    Page<User> listFollowByPage(IPage<User> page, @Param(Constants.WRAPPER)Wrapper<User> queryWrapper,
        long followUserId
    );
}




