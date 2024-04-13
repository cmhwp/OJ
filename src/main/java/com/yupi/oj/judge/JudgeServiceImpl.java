package com.yupi.oj.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.judge.codesandbox.CodeSandbox;
import com.yupi.oj.judge.codesandbox.CodeSandboxFactory;
import com.yupi.oj.judge.codesandbox.CodeSandboxProxy;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.judge.strategy.JudgeContext;
import com.yupi.oj.model.dto.question.JudgeCase;
import com.yupi.oj.model.entity.Question;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.enums.CodeSandBoxStatusEnum;
import com.yupi.oj.model.enums.JudgeInfoMessageEnum;
import com.yupi.oj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.oj.model.vo.QuestionSubmitRunResultResponse;
import com.yupi.oj.service.QuestionService;
import com.yupi.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 判题服务实现类
 **/

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {

        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4）调用沙箱，获取到执行结果
        CodeSandboxFactory factory = CodeSandboxFactory.getInstance();
        CodeSandbox codeSandbox = factory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        // 获取编程语言
        String language = questionSubmit.getLanguage();
        // 获取两端代码
        String frontendCode = questionSubmit.getCode();
        String backendCode = question.getBackendCode();
        // 加工代码,得到完整的代码
        String mergedCode = processCode(frontendCode, backendCode,language);
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> originalInputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 加工输入用例
        List<String> inputList = inputListFactory(originalInputList);

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(mergedCode).language(language).inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 加工错误信息
        String errorMessage = processErrorMessage(executeCodeResponse, backendCode, frontendCode);

        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeCodeResponse.getOutputList());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setError_message(errorMessage);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setOutPut(JSONUtil.toJsonStr(executeCodeResponse.getOutputList()));
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 7）如果输出用例正确题目通过数 + 1
        if (Objects.equals(judgeInfo.getMessage(), JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            boolean result = questionService.update().eq("id", questionId).setSql("acceptedNum = acceptedNum + 1").update();
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目通过数增加失败");
            }
        }
        return questionSubmitService.getById(questionId);
    }

    /**
     * 输入用例工厂（将输入用例转换为可执行的输入用例）
     *
     * @param originalInputList 输入用例
     * @return java.util.List<java.lang.String>
     * @author: LXY
     * @description: TODO
     * @date: 2024/1/30 11:22
     **/
    public List<String> inputListFactory(List<String> originalInputList) {
        List<String> inputList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String input : originalInputList) {
            String[] dateList = input.split(",\\s+");
            for (String date : dateList) {
                String[] parts = date.split("=");
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    sb.append(value).append(" ");
                }
            }
            inputList.add(sb.toString());
            sb.setLength(0);
        }
        return inputList;
    }


    @Override
    public QuestionSubmitRunResultResponse doRun(long questionId, String frontendCode, String language, List<String> inputList) {

        QuestionSubmitRunResultResponse questionSubmitRunResultResponse = new QuestionSubmitRunResultResponse();

        // 调用沙箱，获取到执行结果
        CodeSandboxFactory factory = CodeSandboxFactory.getInstance();
        CodeSandbox codeSandbox = factory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);

        Question question = questionService.getById(questionId);

        // 获取题目逻辑代码
        String logicCode = question.getLogicCode();
        // 根据编程语言确定执行的代码
        String codeToExecute;
        if ("java".equalsIgnoreCase(language)) {
            // 如果是 Java，则使用逻辑代码
            codeToExecute = logicCode;
        } else {
            // 如果不是 Java，将 logicCode 视为 frontendCode
            codeToExecute = frontendCode;
        }
        // 获取逻辑代码得到预计输出
        ExecuteCodeRequest logicCodeCodeRequest = ExecuteCodeRequest.builder().code(codeToExecute).language(language).inputList(inputList).build();
        ExecuteCodeResponse logicCodeResponse = codeSandbox.executeCode(logicCodeCodeRequest);

        if (logicCodeResponse.getStatus() != 1) {
            questionSubmitRunResultResponse.setError_message(logicCodeResponse.getMessage());
            questionSubmitRunResultResponse.setStatus(1);
            return questionSubmitRunResultResponse;
        }

        List<String> expectedOutput = logicCodeResponse.getOutputList();
        questionSubmitRunResultResponse.setExpectedOutput(expectedOutput);

        /*---------------------------------------------------------------------------------------- */
        // 获取判题用例
        List<JudgeCase> judgeCaseList = new ArrayList<>();
        for (String output : expectedOutput) {
            JudgeCase judgeCase = new JudgeCase();
            judgeCase.setOutput(output);
            judgeCaseList.add(judgeCase);
        }
        // 获取后端代码
        String backendCode = question.getBackendCode();
        // 加工代码,得到完整的代码
        String mergedCode = processCode(frontendCode, backendCode,language);
        // 获取输入用例
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(mergedCode).language(language).inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 表示编译错误或执行错误
        if (executeCodeResponse.getStatus() != 1) {
            // 加工错误信息
            String errorMessage = processErrorMessage(executeCodeResponse, backendCode, frontendCode);
            questionSubmitRunResultResponse.setError_message(errorMessage);
            questionSubmitRunResultResponse.setStatus(2);
            questionSubmitRunResultResponse.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            return questionSubmitRunResultResponse;
        }

        questionSubmitRunResultResponse.setActualOutput(executeCodeResponse.getOutputList());

        // 加工错误信息
        String errorMessage = processErrorMessage(executeCodeResponse, backendCode, frontendCode);
        questionSubmitRunResultResponse.setError_message(errorMessage);


        // 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeCodeResponse.getOutputList());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setLanguage(language);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        questionSubmitRunResultResponse.setJudgeInfo(judgeInfo);
        questionSubmitRunResultResponse.setStatus(0);

        return questionSubmitRunResultResponse;
    }


    /**
     * 加工错误信息
     *
     * @param executeCodeResponse
     * @param backendCode
     * @return java.lang.String
     * @author: LXY
     * @description: TODO
     * @date: 2024/1/24 16:30
     **/
    public String processErrorMessage(ExecuteCodeResponse executeCodeResponse, String backendCode, String frontendCode) {
        String errorMessage = null;
        CodeSandBoxStatusEnum codeSandBoxStatusEnum = CodeSandBoxStatusEnum.COMPILE_FAIL;
        if (Objects.equals(executeCodeResponse.getStatus(), codeSandBoxStatusEnum.getValue())) {
            String message = executeCodeResponse.getMessage();

            Pattern pattern1 = Pattern.compile("Main");
            Matcher matcher2 = pattern1.matcher(message);
            String filteredErrorMessage = matcher2.replaceAll("Solution");


            Pattern pattern = Pattern.compile("Line:(\\d+)");
            Matcher matcher = pattern.matcher(filteredErrorMessage);


            int subtractValue = getLineCount(backendCode); // 要减掉的值
            int countMultilingualValue = countMultilineComments(frontendCode); // 要加上的值

            StringBuilder stringBuilder = new StringBuilder();
            while (matcher.find()) {
                int lineNumber = Integer.parseInt(matcher.group(1));
                int newLineNumber = lineNumber - subtractValue + countMultilingualValue;
                matcher.appendReplacement(stringBuilder, "Line: " + newLineNumber);
            }
            matcher.appendTail(stringBuilder);
            errorMessage = stringBuilder.toString();
        }
        return errorMessage;
    }

    /**
     * 结合代码
     *
     * @param frontendCode 前端代码
     * @param backendCode  后端代码
     * @return java.lang.String
     * @author: LXY
     * @description: TODO
     * @date: 2024/1/23 16:04
     **/
    private String processCode(String frontendCode, String backendCode, String language) {
        // 如果语言不是 Java，直接返回前端代码
        if (!"java".equalsIgnoreCase(language)) {
            return frontendCode;
        }
        // 否则，执行现有的合并逻辑
        // String regex = "/\\*([\\s\\S]*?)\\*/";
        // String newCode = frontendCode.replaceAll(regex, "");
        // 提取代码
        String extractedCode = frontendCode.substring(frontendCode.indexOf("Solution {") + 10, frontendCode.lastIndexOf("}"));
        StringBuilder code = new StringBuilder(backendCode);
        // 在第一个代码中找到倒数第2个 `{` 的索引
        int indexOpenBrace = backendCode.lastIndexOf('{');
        code.insert(indexOpenBrace + 1, extractedCode);
        return code.toString();
    }

    /**
     * 获取后端代码插入前的行数
     *
     * @param code
     * @return int
     * @author: LXY
     * @description: TODO
     * @date: 2024/1/23 20:40
     **/
    public static int getLineCount(String code) {
        int indexOpenBrace = code.lastIndexOf('{');
        String[] lines = code.split("\\r?\\n");
        int lineCount = 0;
        int index = 0;
        while (index < code.length() && index <= indexOpenBrace) {
            if (code.charAt(index) == '\n') {
                lineCount++;
            }
            index++;
        }
        return lineCount;
    }

    /**
     * 获取前端代码头部的注解函数行数
     *
     * @param code
     * @return int
     * @author: LXY
     * @description: TODO
     * @date: 2024/1/24 16:41
     **/
    private int countMultilineComments(String code) {
        // 截取获取注释部分代码
        String extractedCode = code.substring(0, code.indexOf("Solution {") + 10);

        String regex = "/\\*([\\s\\S]*?)\\*/";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(extractedCode);
        int count = 0;
        while (matcher.find()) {
            String comment = matcher.group();
            count += comment.split("\n").length;
        }
        return count;
    }

}
