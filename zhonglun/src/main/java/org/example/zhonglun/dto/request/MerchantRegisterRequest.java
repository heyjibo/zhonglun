package org.example.zhonglun.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MerchantRegisterRequest {
    // 用户基础信息
    @NotBlank(message = "登录用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4到20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 30, message = "密码长度必须在8到30个字符之间")
    private String password;

    @NotBlank(message = "联系手机号不能为空")
    @Size(min = 11, max = 11, message = "请输入有效的11位手机号")
    private String phone;

    @NotBlank(message = "联系邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // 商家特定信息
    @NotBlank(message = "商户真实姓名不能为空")
    private String merchantName;

    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "店铺地址不能为空")
    private String address;
}
