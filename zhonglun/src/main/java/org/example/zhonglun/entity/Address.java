package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "t_address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String recipientName;
    private String province;
    private String city;
    private String district;
    private String detailedAddress;
    private int isDefault;
    private Timestamp createTime;
    private Timestamp updateTime;

}
