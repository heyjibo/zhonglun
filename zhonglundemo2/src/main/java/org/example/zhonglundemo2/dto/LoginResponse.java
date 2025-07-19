package org.example.zhonglundemo2.dto;

import lombok.Data;
import org.example.zhonglundemo2.enums.UserRole;

@Data

public class LoginResponse {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
    private String token;

    public LoginResponse(Long id, String email, String name, UserRole role, String token) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.token = token;
    }
}

