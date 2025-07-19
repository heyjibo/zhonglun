package org.example.zhonglundemo2.service;

import org.example.zhonglundemo2.dto.LoginRequest;
import org.example.zhonglundemo2.dto.LoginResponse;
import org.example.zhonglundemo2.dto.RegisterRequest;

public interface AuthService {

    void registerCustomer(RegisterRequest request);

    void registerMerchant(RegisterRequest request);


    void registerAdmin(RegisterRequest request);


    LoginResponse login(LoginRequest request);
}