package com.yupi.oj.judge.codesandbox.iml;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.judge.codesandbox.CodeSandbox;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.oj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {

//    // 定义鉴权请求头和密钥
//    private static final String AUTH_REQUEST_HEADER = "auth";
//
//    private static final String AUTH_REQUEST_SECRET = "secretKey";

    /**
     * 定义鉴权请求头
     */
    private static final String AUTH_REQUEST_HEADER = "sspuoj-codesandbox-auth-by-zzx";

    /**
     * 定义鉴权请求头中的密钥
     */
    private static final String AUTH_REQUEST_SECRET = "$W$~vrZwe7z&L!ht^U%fF2zZzHTjWSwY%@ZeEJ^*(qZ()D3npx";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        // todo 修改成线上的接口
         String url = "http://42.194.144.223:8090/codesandbox/run";
//        String url = "http://localhost:8090/codesandbox/run";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}

