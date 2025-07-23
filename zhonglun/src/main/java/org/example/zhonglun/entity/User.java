package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer userType; // 1-顾客, 2-商家, 3-平台
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
