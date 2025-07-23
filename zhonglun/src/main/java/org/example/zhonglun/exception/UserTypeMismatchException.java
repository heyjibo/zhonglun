package org.example.zhonglun.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户类型不匹配异常。当用户尝试从错误的端点登录时抛出。
 * 继承自 AuthenticationException 以便被 Spring Security 的异常处理链捕获。
 */
public class UserTypeMismatchException extends AuthenticationException {
    public UserTypeMismatchException(String msg) {
        super(msg);
    }
}
