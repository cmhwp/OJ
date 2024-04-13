package com.yupi.oj.judge.codesandbox.model;

import lombok.Data;

@Data
public class JudgeInfo {


    /***
     * 程序执行信息
     */
    private String message;

    /***
     * 消耗时间
     */
    private Long time;

    /***
     * 消耗内存
     */
    private Long memory;
}