package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "t_merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String merchantName;
    private String shopName;
    private String address;
    private int status; // 1-上线, 0-下线
    private Timestamp createTime;
    private Timestamp updateTime;
}
