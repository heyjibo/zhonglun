package org.example.zhonglun.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
}
