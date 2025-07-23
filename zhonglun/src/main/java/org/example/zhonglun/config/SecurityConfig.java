package org.example.zhonglun.config;

import org.example.zhonglun.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用 @PreAuthorize 等注解
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 加密密码
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 配置 Session 管理策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置路由权限
                .authorizeHttpRequests(auth -> auth
                        // 允许所有对认证API的访问 (登录/注册)
                        .requestMatchers("/api/auth/**").permitAll()
                        // 商家端API需要 "MERCHANT" 角色
                        .requestMatchers("/api/merchant/**").hasRole("MERCHANT")
                        // 顾客端API需要 "CUSTOMER" 角色
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        // 平台端API需要 "PLATFORM" 角色
                        .requestMatchers("/api/platform/**").hasRole("PLATFORM")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 4. 添加 JWT 过滤器到 Spring Security 过滤器链中
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
