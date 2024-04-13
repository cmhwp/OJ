package com.yupi.oj.judge.codesandbox;

import com.yupi.oj.judge.codesandbox.iml.ExampleCodeSandbox;
import com.yupi.oj.judge.codesandbox.iml.RemoteCodeSandbox;
import com.yupi.oj.judge.codesandbox.iml.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    // 单例实例
    private static volatile CodeSandboxFactory instance;

    // 私有构造函数，防止外部实例化
    private CodeSandboxFactory() {
    }

    /**
     * 获取单例实例
     *
     * @return 单例实例
     */
    public static CodeSandboxFactory getInstance() {
        if (instance == null) {
            synchronized (CodeSandboxFactory.class) {
                if (instance == null) {
                    instance = new CodeSandboxFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return 代码沙箱实例
     */
    public CodeSandbox newInstance(String type) {
        return switch (type) {
            case "remote" -> new RemoteCodeSandbox();
            case "thirdParty" -> new ThirdPartyCodeSandbox();
            default -> new ExampleCodeSandbox();
        };
    }
}

