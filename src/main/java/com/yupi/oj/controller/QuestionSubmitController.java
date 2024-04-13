package com.yupi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.oj.annotation.AuthCheck;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.DeleteRequest;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.constant.UserConstant;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.model.dto.questionsubmit.*;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.QuestionSubmitConsumptionTimeAndMemoryRankingResponse;
import com.yupi.oj.model.vo.QuestionSubmitRunResultResponse;
import com.yupi.oj.model.vo.QuestionSubmitStatusResponse;
import com.yupi.oj.model.vo.QuestionSubmitVO;
import com.yupi.oj.service.QuestionSubmitService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {


    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson();

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 本次提交题目变化数
     */
    @PostMapping("/doSubmit")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交题目
        final User loginUser = userService.getLoginUser(request);


        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 运行题目
     *
     * @param questionRunAddRequest
     * @param request
     * @return resultNum 本次提交题目变化数
     */
    @PostMapping("/doRun")
    public BaseResponse<QuestionSubmitRunResultResponse> doQuestionRun(@RequestBody QuestionRunAddRequest questionRunAddRequest, HttpServletRequest request) {
        if (questionRunAddRequest == null || questionRunAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交题目
        final User loginUser = userService.getLoginUser(request);


        QuestionSubmitRunResultResponse result = questionSubmitService.doQuestionRun(questionRunAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取题目提交列表
     *
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVoByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        log.info("查询题目提交信息：{}，HTTP请求信息：{}", questionSubmitQueryRequest, request);
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size), questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser);
        return ResultUtils.success(questionSubmitVOPage);
    }

    /**
     * 获取题目状态信息
     *
     * @param questionId
     * @param request
     * @return com.lxy.ikunoj.common.BaseResponse<java.lang.Long>
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/10 17:05
     **/
    @GetMapping("get/status")
    public BaseResponse<Long> getQuestionSubmitStatus(long questionId, HttpServletRequest request) {
        log.info("获取题目提交状态：题目ID：{}, HTTP请求信息：{}", questionId, request);
        if (questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        Long questionSubmitStatus = questionSubmitService.getQuestionSubmitStatus(questionId, userId);
        return ResultUtils.success(questionSubmitStatus);
    }


    /**
     * 获取题目不同难度提交成功数
     *
     * @param difficulty 困难度
     * @param request
     * @return com.lxy.ikunoj.common.BaseResponse<java.lang.Long>
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/10 17:05
     **/
    @GetMapping("get/difficulty_number")
    public BaseResponse<Long> getQuestionSubmitDifficulty(long difficulty, HttpServletRequest request) {
        log.info("获取题目难度：{}, HTTP请求信息：{}", difficulty, request);
        if (difficulty <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        Long QuestionSubmitDifficultyNum = questionSubmitService.getQuestionSubmitDifficulty(difficulty, userId);
        return ResultUtils.success(QuestionSubmitDifficultyNum);
    }

    /**
     * 获取题目不同难度提交通过率
     *
     * @param difficulty 困难度
     * @param request
     * @return com.lxy.ikunoj.common.BaseResponse<java.lang.Long>
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/10 17:05
     **/
    @GetMapping("get/difficulty_pass_rate")
    public BaseResponse<Double> getQuestionSubmitDifficultyPassRate(long difficulty, HttpServletRequest request) {
        if (difficulty <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        Double QuestionSubmitDifficultyPassRateNum = questionSubmitService.getQuestionSubmitDifficultyPassRate(difficulty, userId);
        return ResultUtils.success(QuestionSubmitDifficultyPassRateNum);
    }

    /**
     * 获取题目提交通过率
     *
     * @param request
     * @return com.lxy.ikunoj.common.BaseResponse<java.lang.Long>
     * @author: LXY
     * @description: TODO
     * @date: 2023/12/10 17:05
     **/
    @GetMapping("get/pass_rate")
    public BaseResponse<Double> getQuestionSubmitPassRate(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        Double QuestionSubmitPassRateNum = questionSubmitService.getQuestionSubmitPassRate(userId);
        return ResultUtils.success(QuestionSubmitPassRateNum);
    }


    /**
     * 根据 id 获取题目
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或管理员，不能直接获取所有信息
        if (!questionSubmit.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QuestionSubmitVO questionSubmitVO = questionSubmitService.getQuestionSubmitVO(questionSubmit, loginUser);

        return ResultUtils.success(questionSubmitVO);
    }


    /**
     * 删除提交题目
     *
     * @param deleteRequest
     * @param request
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionSubmit(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        log.info("删除题目ID：{}，HTTP请求信息：{}", deleteRequest, request);
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionSubmit oldquestionsubmit = questionSubmitService.getById(id);
        ThrowUtils.throwIf(oldquestionsubmit == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldquestionsubmit.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionSubmitService.removeById(id);

        return ResultUtils.success(result);
    }


    /**
     * 更新提交题目（仅管理员）
     *
     * @param questionSubmitUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionSubmit(@RequestBody QuestionSubmitUpdateRequest questionSubmitUpdateRequest) {
        if (questionSubmitUpdateRequest == null || questionSubmitUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitUpdateRequest, questionSubmit);

        JudgeInfo judgeInfo = questionSubmitUpdateRequest.getJudgeInfo();
        if (judgeInfo != null) {
            questionSubmit.setJudgeInfo(GSON.toJson(judgeInfo));
        }

        long id = questionSubmitUpdateRequest.getId();
        // 判断是否存在
        QuestionSubmit oldquestionsubmit = questionSubmitService.getById(id);
        ThrowUtils.throwIf(oldquestionsubmit == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionSubmitService.updateById(questionSubmit);

        return ResultUtils.success(result);
    }


    /**
     * 根据当前用户获取题目不同状态列表
     *
     * @return
     */
    @GetMapping("/get/statusQuestionSubmit")
    public BaseResponse<QuestionSubmitStatusResponse> getQuestionSubmitStatusByUser(HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);

        QuestionSubmitStatusResponse questionSubmitStatusResponse = questionSubmitService.getQuestionSubmitStatusByUser(loginUser);

        return ResultUtils.success(questionSubmitStatusResponse);
    }


    /**
     * 获取所有消耗时间和内存的列表
     *
     * @return
     */
    @GetMapping("/get/consumption_Time_Ranking")
    public BaseResponse<QuestionSubmitConsumptionTimeAndMemoryRankingResponse> getQuestionSubmitConsumptionTimeAndMemoryRanking(long questionId, long questionSubmitId, String language, HttpServletRequest request) {
        if (questionId <= 0 || questionSubmitId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (language == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmitConsumptionTimeAndMemoryRankingResponse questionSubmitConsumptionTimeAndMemoryRanking = questionSubmitService.getQuestionSubmitConsumptionTimeAndMemoryRanking(questionId, questionSubmitId, language, request);

        return ResultUtils.success(questionSubmitConsumptionTimeAndMemoryRanking);
    }

}

