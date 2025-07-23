package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderSn;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private String status;
    private Long addressId;
    private Timestamp createTime;
    private Timestamp paymentTime;
    private Timestamp receiveTime;
    private Timestamp updateTime;



}
