package com.yupi.oj.model.vo;

import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
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
public class QuestionSubmitRunResultResponse implements Serializable {

    /***
     *  实际输出
     */
    private List<String> actualOutput;

    /***
     *  预计输出
     */
    private List<String> expectedOutput;

    /**
     * 错误信息
     */
    private String error_message;


    /**
     * 判题信息 （程序执行信 、消耗内存、消耗时间）
     */
    private JudgeInfo judgeInfo;


    /**
     * 执行状态 (0 表示执行成功，1 表示运行逻辑代码失败，2 表示运行用户代码失败)
     */
    private Integer status;


    @Serial
    private static final long serialVersionUID = 1L;
}