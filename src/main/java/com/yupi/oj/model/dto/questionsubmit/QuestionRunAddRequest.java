package com.yupi.oj.model.dto.questionsubmit;

import com.yupi.oj.model.dto.question.JudgeCase;
import com.yupi.oj.model.dto.question.JudgeConfig;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class QuestionRunAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 输入用例
     */
    private List<String[]> inputList;

    @Serial
    private static final long serialVersionUID = 1L;


}