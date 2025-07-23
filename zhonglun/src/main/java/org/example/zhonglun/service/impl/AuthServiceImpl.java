package org.example.zhonglun.service.impl;

import org.example.zhonglun.dto.request.CustomerRegisterRequest;
import org.example.zhonglun.dto.request.LoginRequest;
import org.example.zhonglun.dto.request.MerchantRegisterRequest;
import org.example.zhonglun.entity.Merchant;
import org.example.zhonglun.entity.User;
import org.example.zhonglun.exception.UserAlreadyExistsException;
import org.example.zhonglun.exception.UserTypeMismatchException;
import org.example.zhonglun.repository.MerchantRepository;
import org.example.zhonglun.repository.UserRepository;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.security.JwtUtils;
import org.example.zhonglun.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, MerchantRepository merchantRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String login(LoginRequest request, Integer expectedUserType) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getUser().getUserType().equals(expectedUserType)) {
            throw new UserTypeMismatchException("认证失败：用户类型不匹配，请从正确的入口登录。");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateToken(userDetails);
    }

    @Override
    public void registerCustomer(CustomerRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("注册失败：该用户名已被使用！");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("注册失败：该邮箱已被注册！");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUserType(1); // 1-顾客
        user.setStatus(1);   // 1-正常
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void registerMerchant(MerchantRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("注册失败：该登录用户名已被使用！");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("注册失败：该联系邮箱已被注册！");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUserType(2); // 2-商家
        user.setStatus(0);   // 0-待审核, 1-正常. 商家注册后通常需要平台审核
        User savedUser = userRepository.save(user);

        Merchant merchant = new Merchant();
        merchant.setUserId(savedUser.getId());
        merchant.setMerchantName(request.getMerchantName());
        merchant.setShopName(request.getShopName());
        merchant.setAddress(request.getAddress());
        merchant.setStatus(0); // 0-待审核
        merchantRepository.save(merchant);
    }
}
