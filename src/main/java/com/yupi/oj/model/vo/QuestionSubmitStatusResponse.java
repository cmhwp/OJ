package com.yupi.oj.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 题目提交封装类
 *
 * @TableName question
 */
@Data
public class QuestionSubmitStatusResponse implements Serializable {

    /***
     *  当前用户题目通过id列表
     */
    private List<Long> questionPassIds;

    /***
     *  当前用户题目未通过id列表 （尝试过）
     */
    private List<Long> questionNotPassIds;


    @Serial
    private static final long serialVersionUID = 1L;
}
