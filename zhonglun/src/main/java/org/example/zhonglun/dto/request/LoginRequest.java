package org.example.zhonglun.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 通用登录请求体
 * 由于用户类型由API路径决定，这里只需用户名和密码。
 */
public record LoginRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 4, max = 20, message = "用户名长度不正确")
        String username,

        @NotBlank(message = "密码不能为空")
        String password
) {
}
