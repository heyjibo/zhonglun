package org.example.zhonglun.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private Integer userType;
    private Collection<?> roles; // e.g., ["ROLE_CUSTOMER"]
}
