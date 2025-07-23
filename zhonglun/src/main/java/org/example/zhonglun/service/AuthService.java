package org.example.zhonglun.service;

import org.example.zhonglun.dto.request.CustomerRegisterRequest;
import org.example.zhonglun.dto.request.LoginRequest;
import org.example.zhonglun.dto.request.MerchantRegisterRequest;

public interface AuthService {
    String login(LoginRequest request, Integer expectedUserType);
    void registerCustomer(CustomerRegisterRequest request);
    void registerMerchant(MerchantRegisterRequest request);
}
