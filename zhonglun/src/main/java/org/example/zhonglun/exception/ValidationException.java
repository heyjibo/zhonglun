package org.example.zhonglun.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 业务逻辑校验异常
 * 例如：库存不足、操作不被允许等。
 * Spring MVC会自动将其映射为 HTTP 400 Bad Request 状态码。
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
