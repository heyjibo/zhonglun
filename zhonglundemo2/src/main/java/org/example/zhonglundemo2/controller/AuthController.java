package org.example.zhonglundemo2.controller;

import org.example.zhonglundemo2.dto.LoginRequest;
import org.example.zhonglundemo2.dto.LoginResponse;
import org.example.zhonglundemo2.dto.RegisterRequest;
import org.example.zhonglundemo2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/customer/register")
    public ResponseEntity<Void> registerCustomer(@RequestBody RegisterRequest request) {
        authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/merchant/register")
    public ResponseEntity<Void> registerMerchant(@RequestBody RegisterRequest request) {
        authService.registerMerchant(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}