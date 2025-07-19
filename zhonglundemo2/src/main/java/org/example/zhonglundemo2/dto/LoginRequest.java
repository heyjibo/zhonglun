package org.example.zhonglundemo2.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
