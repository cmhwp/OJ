package com.yupi.oj.model.enums;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 沙箱状态信息枚举
 */

public enum CodeSandBoxStatusEnum {

    COMPILE_FAIL("编译失败", 2),

    RUNTIME_SUCCEED("运行成功", 1),

    EXECUTION_FAIL("执行失败", 3);


    private final String text;

    private final Integer value;

    CodeSandBoxStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static CodeSandBoxStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CodeSandBoxStatusEnum anEnum : CodeSandBoxStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

}
