package org.example.zhonglun.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateAddressRequest {

    @NotEmpty(message = "收货人姓名不能为空")
    private String receiver;

    @NotEmpty(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @NotEmpty(message = "省份不能为空")
    private String province;

    @NotEmpty(message = "城市不能为空")
    private String city;

    @NotEmpty(message = "区/县不能为空")
    private String district;

    @NotEmpty(message = "详细地址不能为空")
    private String detail;

    private Integer isDefault; // 0-否, 1-是
}
