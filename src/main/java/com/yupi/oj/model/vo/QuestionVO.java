package com.yupi.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yupi.oj.model.dto.question.JudgeCase;
import com.yupi.oj.model.dto.question.JudgeConfig;
import com.yupi.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 * @TableName question
 */
@TableName(value ="question")
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 前端代码
     */
    private String frontendCode;


    /**
     * 题目通过率
     */
    private BigDecimal passRate;

    /**
     * 难度 1-简单 2-中等 3-困难
     */
    private Integer difficulty;

    /**
     * 判题用例
     */
    private List<String> judgeCase;

    /**
     * 输入用例
     */
    private List<String[]> input;


    /**
     * 输入用例名称
     */
    private List<String[]> inputListName;


    /**
     * 输出用例
     */
    private List<String> output;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;

    /**
     * 判题状态（1 - 未开始、2 - 尝试过、3 - 已通过）
     */
    private Integer status;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        List<String> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), String.class);
        List<String> judgeCaseDetailList = new ArrayList<>();
        List<String> inputList = new ArrayList<>();
        List<String> outputList = new ArrayList<>();
        for (String judgeCaseStr : judgeCaseList) {
            JudgeCase judgeCase = JSONUtil.toBean(judgeCaseStr, JudgeCase.class);
            String input = judgeCase.getInput();
            String output = judgeCase.getOutput();
            judgeCaseDetailList.add(judgeCase.toString());
            inputList.add(input);
            outputList.add(output);
        }
        List<String[]> inputListName = getStringName(inputList);
        List<String[]> inputListString = getStringInput(inputList);
        questionVO.setJudgeCase(judgeCaseDetailList);
        questionVO.setInput(inputListString);
        questionVO.setInputListName(inputListName);
        questionVO.setOutput(outputList);
        return questionVO;
    }

    @NotNull
    private static List<String[]> getStringName(List<String> inputList) {
        List<String[]> inputListName = new ArrayList<>();
        List<String> Name = new ArrayList<>();
        for (String input : inputList) {
            String[] dateList = input.split(",\\s+");
            for (String date : dateList) {
                String[] parts = date.split("=");
                if (parts.length == 2) {
                    String value = parts[0].trim();
                    Name.add(value);
                }
            }
            inputListName.add(Name.toArray(new String[0]));
            Name.clear();
        }
        return inputListName;
    }

    @NotNull
    private static List<String[]> getStringInput(List<String> inputList) {
        List<String[]> inputListName = new ArrayList<>();
        List<String> Name = new ArrayList<>();
        for (String input : inputList) {
            String[] dateList = input.split(",\\s+");
            for (String date : dateList) {
                String[] parts = date.split("=");
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    Name.add(value);
                }
            }
            inputListName.add(Name.toArray(new String[0]));
            Name.clear();
        }
        return inputListName;
    }

    @Serial
    private static final long serialVersionUID = 1L;
}