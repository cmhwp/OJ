package com.yupi.oj.judge.strategy;

import com.yupi.oj.model.dto.question.JudgeCase;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.model.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    /**
     * 判题信息 （程序执行信 、消耗内存、消耗时间）
     */
    private JudgeInfo judgeInfo;

    /**
     * 输入用例
     */
    private List<String> inputList;

    /**
     * 输出用例
     */
    private List<String> outputList;


    private List<JudgeCase> judgeCaseList;
    /***
     * 题目信息
     */
    private Question question;

    /***
     * 语言
     */
    private String language;

}