package com.yupi.oj.model.dto.questionsubmit;

import com.yupi.oj.common.PageRequest;
import com.yupi.oj.model.dto.question.JudgeCase;
import com.yupi.oj.model.dto.question.JudgeConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目状态
     */
    private String message;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 判题状态
     */
    private Integer status;


    /**
     * 消耗内存
     */
    private Integer memory;

    /**
     * 消耗时间
     */
    private Integer time;

    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 用户 id
     */
    private Long userId;


    /**
     * 搜索词
     */
    private String searchText;

    @Serial
    private static final long serialVersionUID = 1L;
}