package org.example.zhonglun.controller;

import jakarta.validation.Valid;
import org.example.zhonglun.dto.ApiResponse;
import org.example.zhonglun.dto.request.CustomerRegisterRequest;
import org.example.zhonglun.dto.request.LoginRequest;
import org.example.zhonglun.dto.request.MerchantRegisterRequest;
import org.example.zhonglun.dto.response.JwtResponse;
import org.example.zhonglun.dto.response.UserInfoResponse;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 统一认证授权控制器
 * 严格按照三端分离原则，提供独立的注册、登录API端点。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --- 顾客端认证 API ---

    @PostMapping("/customer/login")
    public ResponseEntity<JwtResponse> loginCustomer(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.login(loginRequest, 1); // 1 代表顾客
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/customer/register")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody CustomerRegisterRequest registerRequest) {
        authService.registerCustomer(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "顾客账户注册成功！"));
    }

    // --- 商家端认证 API ---

    @PostMapping("/merchant/login")
    public ResponseEntity<JwtResponse> loginMerchant(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.login(loginRequest, 2); // 2 代表商家
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/merchant/register")
    public ResponseEntity<ApiResponse> registerMerchant(@Valid @RequestBody MerchantRegisterRequest registerRequest) {
        authService.registerMerchant(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "商家账户注册成功，等待平台审核。"));
    }

    // --- 平台端认证 API ---

    @PostMapping("/platform/login")
    public ResponseEntity<JwtResponse> loginPlatform(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.login(loginRequest, 3); // 3 代表平台管理员
        return ResponseEntity.ok(new JwtResponse(jwt));
    }


    // --- 通用 API ---

    /**
     * 获取当前已登录用户的信息
     * 任何成功登录的用户（顾客、商家、平台）都可以调用此接口。
     * @return 当前用户的基本信息
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // Spring Security 注解，确保只有已认证用户能访问
    public ResponseEntity<UserInfoResponse> getCurrentUser(@NonNull Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserInfoResponse userInfo = new UserInfoResponse(
                userDetails.getUser().getId(),
                userDetails.getUsername(),
                userDetails.getUser().getUserType(),
                userDetails.getAuthorities()
        );

        return ResponseEntity.ok(userInfo);
    }
}
