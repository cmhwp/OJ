package com.yupi.oj.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author SEU
* @description 针对表【question_favour(题目收藏)】的数据库操作Mapper
* @createDate 2024-03-09 15:03:38
* @Entity com.yupi.oj.model.entity.QuestionFavour
*/
public interface QuestionFavourMapper extends BaseMapper<QuestionFavour> {

    /**
     * 分页查询收藏帖子列表
     *
     * @param page
     * @param queryWrapper
     * @param favourUserId
     * @return
     */
    Page<Question> listFavourQuestionByPage(IPage<Question> page, Wrapper<Question> queryWrapper, long favourUserId);
}



