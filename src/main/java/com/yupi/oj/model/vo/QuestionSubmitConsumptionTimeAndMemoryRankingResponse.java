package com.yupi.oj.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class QuestionSubmitConsumptionTimeAndMemoryRankingResponse implements Serializable {

    /***
     *  消耗时间列表
     */
    private List<Long> timeList;

    private List<Double> timePercentageList;
    /***
     *  消耗内存列表
     */
    private List<Long> memoryList;

    private List<Double> memoryPercentageList;

    private Double timePercentageSum;

    private Double memoryPercentageSum;


    @Serial
    private static final long serialVersionUID = 1L;

}