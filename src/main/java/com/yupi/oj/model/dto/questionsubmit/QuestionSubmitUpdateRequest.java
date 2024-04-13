package com.yupi.oj.model.dto.questionsubmit;

import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 题目提交
 *
 * @TableName question_submit
 */
@Data
public class QuestionSubmitUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 错误信息
     */
    private String error_message;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 实际输出用例
     */
    private String outPut;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;


    /**
     * 是否删除
     */
    private Integer isDelete;

    @Serial
    private static final long serialVersionUID = 1L;
}