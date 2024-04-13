package com.yupi.oj.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.constant.CommonConstant;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.exception.ThrowUtils;
import com.yupi.oj.mapper.QuestionSubmitMapper;
import com.yupi.oj.model.dto.question.QuestionQueryRequest;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.QuestionDifficultyResponse;
import com.yupi.oj.model.vo.QuestionVO;
import com.yupi.oj.model.vo.UserVO;
import com.yupi.oj.service.QuestionService;
import com.yupi.oj.mapper.QuestionMapper;
import com.yupi.oj.service.UserService;
import com.yupi.oj.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author SEU
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-03-09 15:01:30
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {


    @Resource
    private UserService userService;

    @Resource
    private QuestionMapper questionMapper;


    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Integer difficulty = questionQueryRequest.getDifficulty();
        String searchText = questionQueryRequest.getSearchText();
        List<Long> containIds = questionQueryRequest.getContainIds();
        List<Long> notContainIds = questionQueryRequest.getNotContainIds();


        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText).or().like("userId", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.in(CollectionUtils.isNotEmpty(containIds), "id", containIds); // 使用 in 条件查询 ids 列表中的ID
        queryWrapper.notIn(CollectionUtils.isNotEmpty(notContainIds), "id", notContainIds); // 使用 notIn 条件查询不包含在ids列表中的ID
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);

        int questionSubmitStatus = getQuestionSubmitStatus(question.getId(), request);
        if (questionSubmitStatus != -1) {
            questionVO.setStatus(questionSubmitStatus);
        }
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        List<QuestionVO> questionVOList = questionList.stream().map(question -> getQuestionVO(question, request)).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * 题目困难数
     *
     * @return long
     **/
    @Override
    public QuestionDifficultyResponse getQuestionDifficulty(Long userId) {
        QuestionDifficultyResponse questionDifficultyResponse = new QuestionDifficultyResponse();

        QueryWrapper<Question> simpleQueryWrapper = new QueryWrapper<>();
        simpleQueryWrapper.eq("isDelete", false);
        simpleQueryWrapper.eq("difficulty", 1);
        Double simpleQuestionNum = Double.valueOf(questionMapper.selectCount(simpleQueryWrapper));
        questionDifficultyResponse.setSimpleQuestionNum(simpleQuestionNum);


        QueryWrapper<Question> mediumQueryWrapper = new QueryWrapper<>();
        mediumQueryWrapper.eq("isDelete", false);
        mediumQueryWrapper.eq("difficulty", 2);
        Double mediumQuestionNum = Double.valueOf(questionMapper.selectCount(mediumQueryWrapper));
        questionDifficultyResponse.setMediumQuestionNum(mediumQuestionNum);


        QueryWrapper<Question> difficultQueryWrapper = new QueryWrapper<>();
        difficultQueryWrapper.eq("isDelete", false);
        difficultQueryWrapper.eq("difficulty", 3);
        Double difficultQuestionNum = Double.valueOf(questionMapper.selectCount(difficultQueryWrapper));
        questionDifficultyResponse.setDifficultQuestionNum(difficultQuestionNum);

        // ----------------------------------------------------------------
        // 获取当前用户的提交记录
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(org.apache.commons.lang3.ObjectUtils.isNotEmpty(userId), "userId", userId);
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);

        // 使用Set确定唯一ID
        Set<Long> acceptedQuestionIds = new HashSet<>();
        for (QuestionSubmit questionSubmit : questionSubmitList) {
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            String message = judgeInfo.getMessage();
            if (Objects.equals(message, "Accepted")) {
                Long questionId = questionSubmit.getQuestionId();
                acceptedQuestionIds.add(questionId);
            }
        }

        double simplePassNum = 0;
        for (Long id : acceptedQuestionIds) {
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq(org.apache.commons.lang3.ObjectUtils.isNotEmpty(id), "id", id);
            questionQueryWrapper.eq("difficulty", 1);
            simplePassNum += questionMapper.selectCount(questionQueryWrapper);
        }
        questionDifficultyResponse.setSimplePassNum(simplePassNum);

        // ----------------------------------------------------------------
        double mediumPassNum = 0;
        for (Long id : acceptedQuestionIds) {
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq(org.apache.commons.lang3.ObjectUtils.isNotEmpty(id), "id", id);
            questionQueryWrapper.eq("difficulty", 2);
            mediumPassNum += questionMapper.selectCount(questionQueryWrapper);
        }
        questionDifficultyResponse.setMediumPassNum(mediumPassNum);
        double difficultyPassNum = 0;
        for (Long id : acceptedQuestionIds) {
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq(org.apache.commons.lang3.ObjectUtils.isNotEmpty(id), "id", id);
            questionQueryWrapper.eq("difficulty", 3);
            difficultyPassNum += questionMapper.selectCount(questionQueryWrapper);
        }
        questionDifficultyResponse.setDifficultyPassNum(difficultyPassNum);


        questionDifficultyResponse.setSimpleSubmissionPassRateNum(simpleQuestionNum == 0 ? 0 : simplePassNum / simpleQuestionNum);
        questionDifficultyResponse.setMediumSubmissionPassRateNum(mediumQuestionNum == 0 ? 0 : mediumPassNum / mediumQuestionNum);
        questionDifficultyResponse.setDifficultySubmissionPassRateNum(difficultQuestionNum == 0 ? 0 : difficultyPassNum / difficultQuestionNum);

        double throughNumber = simplePassNum + mediumPassNum + difficultyPassNum;
        questionDifficultyResponse.setThroughNumber(throughNumber);

        double questionSumNumber = simpleQuestionNum + mediumQuestionNum + difficultQuestionNum;
        questionDifficultyResponse.setQuestionSumNumber(questionSumNumber);

        questionDifficultyResponse.setPassRateNum(questionSumNumber == 0 ? 0 : throughNumber / questionSumNumber);

        return questionDifficultyResponse;
    }

    @Override
    public Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = this.page(new Page<>(current, size), this.getQueryWrapper(questionQueryRequest));

        return this.getQuestionVOPage(questionPage, request);
    }

    public int getQuestionSubmitStatus(long questionId, HttpServletRequest request) {
        User loginUser;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            return -1;
        }
        Long userId = loginUser.getId();
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);
        // 未找到
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return 1;
        }

        for (QuestionSubmit questionSubmit : questionSubmitList) {
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            String message = judgeInfo.getMessage();
            if (Objects.equals(message, "Accepted")) {
                return 3;
            }
        }
        return 2;
    }

}





