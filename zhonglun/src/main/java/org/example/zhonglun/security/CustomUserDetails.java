package org.example.zhonglun.security;

import org.example.zhonglun.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        Integer userType = this.user.getUserType();

        if (userType == null) {
            // 默认或无类型时，给予最基础的角色
            role = "ROLE_USER";
        } else {
            role = switch (userType) {
                case 1 -> "ROLE_CUSTOMER";
                case 2 -> "ROLE_MERCHANT";
                case 3 -> "ROLE_ADMIN"; // 假设平台是管理员
                default -> "ROLE_USER";
            };
        }
        // Spring Security 需要 "ROLE_" 前缀来进行角色判断
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getEmail() {
        return this.user.getEmail();
    }

    // 其他 isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled 方法
    // 为简化，都返回 true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return user.getStatus() == 1; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.getStatus() == 1; }
}