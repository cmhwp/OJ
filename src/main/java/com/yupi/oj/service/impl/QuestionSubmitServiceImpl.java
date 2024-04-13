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
import com.yupi.oj.judge.JudgeService;
import com.yupi.oj.mapper.QuestionMapper;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.model.dto.questionsubmit.QuestionRunAddRequest;
import com.yupi.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.enums.QuestionSubmitLanguageEnum;
import com.yupi.oj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.oj.model.vo.*;
import com.yupi.oj.service.QuestionService;
import com.yupi.oj.service.QuestionSubmitService;
import com.yupi.oj.mapper.QuestionSubmitMapper;
import com.yupi.oj.service.UserService;
import com.yupi.oj.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @author SEU
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-03-09 15:03:49
*/
/**
 *
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    private QuestionMapper questionMapper;


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        boolean result = questionService.update().eq("id", questionId).setSql("submitNum = submitNum + 1").update();
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数增加失败");
        }
        Long questionSubmitId = questionSubmit.getId();

        /*   CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });*/

        // 执行判题服务
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });
        future.join(); // 等待异步任务执行完毕
        return questionSubmitId;
    }

    /**
     * 运行题目
     *
     * @param questionRunAddRequest
     * @param loginUser
     * @return1
     */
    @Override
    public QuestionSubmitRunResultResponse doQuestionRun(QuestionRunAddRequest questionRunAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionRunAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 校验提交代码是否为空
        String code = questionRunAddRequest.getCode();
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交代码为空");
        }
        // 校验输入用例是否为空
        List<String[]> originalInputList = questionRunAddRequest.getInputList();
        if (originalInputList == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入用例为空");
        }
        // 加工输入用例
        List<String> inputListResult = new ArrayList<>();
        for (String[] date : originalInputList) {
            StringBuilder sb = new StringBuilder();
            for (String input : date) {
                sb.append(input).append(" ");
            }
            inputListResult.add(sb.toString());
            sb.setLength(0);
        }


        long questionId = questionRunAddRequest.getQuestionId();
        // 判断实体是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 执行运行服务
        AtomicReference<QuestionSubmitRunResultResponse> questionSubmitRunResultVO = new AtomicReference<>(new QuestionSubmitRunResultResponse());
        CompletableFuture.runAsync(() -> {
            questionSubmitRunResultVO.set(judgeService.doRun(questionId, code, language, inputListResult));
        }).join();
        return questionSubmitRunResultVO.get();

    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        String message = questionSubmitQueryRequest.getMessage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        String searchText = questionSubmitQueryRequest.getSearchText();
        Integer time = questionSubmitQueryRequest.getTime();
        Integer memory = questionSubmitQueryRequest.getMemory();

        // 拼接查询条件

        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        if (StringUtils.isNotBlank(message)) {
            queryWrapper.apply("JSON_CONTAINS(judgeInfo, '{\"message\": \"" + message + "\"}')");
        }
        if (time != null) {
            queryWrapper.apply("JSON_EXTRACT(judgeInfo, '$.time') = {0}", time);
        }
        if (memory != null) {
            queryWrapper.apply("JSON_EXTRACT(judgeInfo, '$.memory') = {0}", memory);
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("questionId", searchText).or().like("id", searchText).or().like("userId", searchText);
        }
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Long questionId = questionSubmitVO.getQuestionId();

        Question question = questionService.getById(questionId);
        QuestionVO questionVO = QuestionVO.objToVo(question);

        questionSubmitVO.setQuestionVO(questionVO);

        Long submitVOUserId = questionSubmitVO.getUserId();
        User user = null;
        if (submitVOUserId != null) {
            user = userService.getById(submitVOUserId);
        }
        UserVO userVO = userService.getUserVO(user);

        questionSubmitVO.setUserVO(userVO);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        // long userId = loginUser.getId();
        // 处理脱敏
        // if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
        //       questionSubmitVO.setCode(null);
        // }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser)).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public long getQuestionSubmitStatus(long questionId, long userId) {
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

    @Override
    public QuestionSubmitStatusResponse getQuestionSubmitStatusByUser(User loginUser) {

        QuestionSubmitStatusResponse questionSubmitStatusResponse = new QuestionSubmitStatusResponse();
        Long userId = loginUser.getId();
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);
        // 未找到
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitStatusResponse;
        }
        // 使用Set确定唯一ID
        Set<Long> acceptedQuestionIds = new HashSet<>();  // 通过题目
        Set<Long> notAcceptedQuestionIds = new HashSet<>();  // 通过题目
        for (QuestionSubmit questionSubmit : questionSubmitList) {
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            String message = judgeInfo.getMessage();
            Long questionId = questionSubmit.getQuestionId();
            if (Objects.equals(message, "Accepted")) {
                acceptedQuestionIds.add(questionId);
            } else {
                notAcceptedQuestionIds.add(questionId);
            }
        }
        notAcceptedQuestionIds.removeAll(acceptedQuestionIds); // 过滤掉相同的元素

        List<Long> acceptedQuestionIdList = new ArrayList<>(acceptedQuestionIds);
        List<Long> notAcceptedQuestionIdList = new ArrayList<>(notAcceptedQuestionIds);
        questionSubmitStatusResponse.setQuestionPassIds(acceptedQuestionIdList);
        questionSubmitStatusResponse.setQuestionNotPassIds(notAcceptedQuestionIdList);

        return questionSubmitStatusResponse;
    }


    @Override
    public long getQuestionSubmitDifficulty(long difficulty, Long userId) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 查询当前用户提交的题目
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);
        long questionSubmitDifficultyNum = 0;

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

        for (Long id : acceptedQuestionIds) {
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
            questionSubmitDifficultyNum += questionMapper.selectCount(questionQueryWrapper);
        }
        return questionSubmitDifficultyNum;
    }

    @Override
    public double getQuestionSubmitDifficultyPassRate(long difficulty, Long userId) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // 查询当前用户提交的题目
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);
        // 通过
        List<Long> acceptedQuestionIds = new ArrayList<>();
        // 统计当前难度的题目提交数量
        double questionSubmitDifficultyNum = 0;
        // 统计当前难度的题目提交通过数量
        double questionSubmitDifficultyAcceptedNum = 0;
        for (QuestionSubmit questionSubmit : questionSubmitList) {
            Long id = questionSubmit.getQuestionId();
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            String message = judgeInfo.getMessage();
            if (Objects.equals(message, "Accepted")) {
                Long questionId = questionSubmit.getQuestionId();
                acceptedQuestionIds.add(questionId);
            }
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            queryWrapper.eq("isDelete", false);
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
            questionSubmitDifficultyNum += questionMapper.selectCount(questionQueryWrapper);
        }

        for (Long acceptedId : acceptedQuestionIds) {
            QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
            queryWrapper.eq("isDelete", false);
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(acceptedId), "id", acceptedId);
            questionQueryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
            questionSubmitDifficultyAcceptedNum += questionMapper.selectCount(questionQueryWrapper);
        }

        return questionSubmitDifficultyNum == 0 ? 0 : questionSubmitDifficultyAcceptedNum / questionSubmitDifficultyNum;
    }

    @Override
    public double getQuestionSubmitPassRate(Long userId) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 查询当前用户提交的题目
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(queryWrapper);
        double questionSubmitNum = questionSubmitList.size();
        double questionSubmitAcceptedNum = 0;

        for (QuestionSubmit questionSubmit : questionSubmitList) {
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            String message = judgeInfo.getMessage();
            if (Objects.equals(message, "Accepted")) {
                questionSubmitAcceptedNum += 1;
            }
        }
        return questionSubmitNum == 0 ? 0 : questionSubmitAcceptedNum / questionSubmitNum;
    }

    @Override
    public QuestionSubmitConsumptionTimeAndMemoryRankingResponse getQuestionSubmitConsumptionTimeAndMemoryRanking(long questionId, long questionSubmitId, String language, HttpServletRequest request) {

        QuestionSubmitConsumptionTimeAndMemoryRankingResponse questionSubmitConsumptionTimeAndMemoryRankingResponse = new QuestionSubmitConsumptionTimeAndMemoryRankingResponse();

        QueryWrapper<QuestionSubmit> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("questionId", questionId);
        questionQueryWrapper.eq("language", language);
        questionQueryWrapper.eq("isDelete", false);
        questionQueryWrapper.apply("JSON_CONTAINS(judgeInfo, '{\"message\": \"" + "Accepted" + "\"}')");
        List<QuestionSubmit> questionSubmitList = questionSubmitMapper.selectList(questionQueryWrapper);
        // 使用Map来计数
        Map<Long, Integer> consumptionTimesCount = new HashMap<>();
        Map<Long, Integer> consumptionMemoriesCount = new HashMap<>();


        for (QuestionSubmit questionSubmit : questionSubmitList) {
            String judgeInfoStr = questionSubmit.getJudgeInfo();
            JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
            Long time = judgeInfo.getTime();
            Long memory = judgeInfo.getMemory();
            consumptionTimesCount.put(time, consumptionTimesCount.getOrDefault(time, 0) + 1);
            consumptionMemoriesCount.put(memory, consumptionMemoriesCount.getOrDefault(memory, 0) + 1);
        }

        // 计算总提交次数
        int totalSubmissions = questionSubmitList.size();

        // 计算每个时间和内存的占比
        // 使用TreeMap替代LinkedHashMap以自动排序
        Map<Long, Double> timePercentages = new TreeMap<>();
        Map<Long, Double> memoryPercentages = new TreeMap<>();

        for (Map.Entry<Long, Integer> entry : consumptionTimesCount.entrySet()) {
            BigDecimal percentage = BigDecimal.valueOf((double) entry.getValue() / totalSubmissions * 100);
            percentage = percentage.setScale(2, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
            timePercentages.put(entry.getKey(), percentage.doubleValue());
        }

        for (Map.Entry<Long, Integer> entry : consumptionMemoriesCount.entrySet()) {
            BigDecimal percentage = BigDecimal.valueOf((double) entry.getValue() / totalSubmissions * 100);
            percentage = percentage.setScale(2, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
            memoryPercentages.put(entry.getKey(), percentage.doubleValue());
        }

        // 将timePercentages拆分成两个List
        List<Long> timeList = new ArrayList<>();
        List<Double> timePercentageList = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : timePercentages.entrySet()) {
            timeList.add(entry.getKey());
            timePercentageList.add(entry.getValue());
        }

        // 将memoryPercentages拆分成两个List
        List<Long> memoryList = new ArrayList<>();
        List<Double> memoryPercentageList = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : memoryPercentages.entrySet()) {
            memoryList.add(entry.getKey());
            memoryPercentageList.add(entry.getValue());
        }

        QueryWrapper<QuestionSubmit> questionSubmitqueryWrapper = new QueryWrapper<>();
        questionSubmitqueryWrapper.eq("id", questionSubmitId);
        questionSubmitqueryWrapper.eq("isDelete", false);
        QuestionSubmit questionSubmit = questionSubmitMapper.selectOne(questionSubmitqueryWrapper);

        if (questionSubmit == null) {
            // 没有找到对应的提交记录，返回空响应
            return questionSubmitConsumptionTimeAndMemoryRankingResponse;
        }

        String judgeInfoStr = questionSubmit.getJudgeInfo();
        JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
        Long submitTime = judgeInfo.getTime();
        Long submitMemory = judgeInfo.getMemory();

        // 查找 timePercentages 中的排名位置
        int timeRankingIndex = 0;
        for (Map.Entry<Long, Double> entry : timePercentages.entrySet()) {
            if (entry.getKey().equals(submitTime)) {
                break;
            }
            timeRankingIndex++;
        }

        // 查找 memoryPercentages 中的排名位置
        int memoryRankingIndex = 0;
        for (Map.Entry<Long, Double> entry : memoryPercentages.entrySet()) {
            if (entry.getKey().equals(submitMemory)) {
                break;
            }
            memoryRankingIndex++;
        }

        // 计算 timePercentages 和 memoryPercentages 中排名后面的百分比总和
        double timePercentageSum = 0.0;
        double memoryPercentageSum = 0.0;
        for (int i = timeRankingIndex + 1; i < timePercentageList.size(); i++) {
            timePercentageSum += timePercentageList.get(i);
        }
        for (int i = memoryRankingIndex + 1; i < memoryPercentageList.size(); i++) {
            memoryPercentageSum += memoryPercentageList.get(i);
        }

        // 使用 DecimalFormat 格式化保留两位小数
        DecimalFormat df = new DecimalFormat("#.00");
        timePercentageSum = Double.parseDouble(df.format(timePercentageSum));
        memoryPercentageSum = Double.parseDouble(df.format(memoryPercentageSum));

        questionSubmitConsumptionTimeAndMemoryRankingResponse.setTimeList(timeList);
        questionSubmitConsumptionTimeAndMemoryRankingResponse.setTimePercentageList(timePercentageList);
        questionSubmitConsumptionTimeAndMemoryRankingResponse.setMemoryList(memoryList);
        questionSubmitConsumptionTimeAndMemoryRankingResponse.setMemoryPercentageList(memoryPercentageList);

        if (timePercentageSum != 0.0) {
            questionSubmitConsumptionTimeAndMemoryRankingResponse.setTimePercentageSum(timePercentageSum);
        } else {
            questionSubmitConsumptionTimeAndMemoryRankingResponse.setTimePercentageSum(0.01);
        }
        if (memoryPercentageSum != 0.0) {
            questionSubmitConsumptionTimeAndMemoryRankingResponse.setMemoryPercentageSum(memoryPercentageSum);
        } else {
            questionSubmitConsumptionTimeAndMemoryRankingResponse.setMemoryPercentageSum(0.01);
        }


        return questionSubmitConsumptionTimeAndMemoryRankingResponse;
    }
}




