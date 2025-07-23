package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long merchantId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer salesVolume;
    private Integer status; // 1-上架, 0-下架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
