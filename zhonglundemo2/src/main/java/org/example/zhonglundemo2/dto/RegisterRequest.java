package org.example.zhonglundemo2.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class RegisterRequest {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Name is required")
    private String name;

    private String phone;
    private String address;
    private String businessName;
    private String contactPerson;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getContactPerson() {
        return contactPerson;
    }
}
