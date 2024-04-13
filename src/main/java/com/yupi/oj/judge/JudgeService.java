package com.yupi.oj.judge;

import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.vo.QuestionSubmitRunResultResponse;

import java.util.List;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);


    /**
     * 运行
     *
     * @return
     */
    QuestionSubmitRunResultResponse doRun(long questionId, String code, String language, List<String> inputList);
}
