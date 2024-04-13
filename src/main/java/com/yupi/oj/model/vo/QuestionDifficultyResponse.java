package com.yupi.oj.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 题目提交封装类
 *
 * @TableName question
 */
@Data
public class QuestionDifficultyResponse implements Serializable {


    /***
     * 简单题目总数量
     */
    private Double simpleQuestionNum;

    /***
     * 中等题目总数量
     */
    private Double mediumQuestionNum;

    /***
     * 困难题目总数量
     */
    private Double difficultQuestionNum;


    /***
     * 简单题目通过数量（当前用户）
     */
    private Double simplePassNum;


    /***
     * 中等题目通过数量（当前用户）
     */
    private Double mediumPassNum;

    /***
     * 困难题目通过数量（当前用户）
     */
    private Double difficultyPassNum;


    /***
     * 简单题目通过率（当前用户）
     */
    private Double simpleSubmissionPassRateNum;


    /***
     * 中等题目通过率（当前用户）
     */
    private Double mediumSubmissionPassRateNum;

    /***
     * 困难题目通过率（当前用户）
     */
    private Double difficultySubmissionPassRateNum;


    /***
     * 通过总数
     */
    private Double throughNumber;


    /***
     * 题目总数
     */
    private Double questionSumNumber;

    /***
     * 总通过率
     */
    private Double passRateNum;



    @Serial
    private static final long serialVersionUID = 1L;
}
