package com.clint.mybilibili.domain.exception;

import lombok.Data;

/**
 * 条件异常类
 */
@Data
public class ConditionException extends RuntimeException {

    /**
     * 状态码
     */
    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        code = "500";
    }
}
