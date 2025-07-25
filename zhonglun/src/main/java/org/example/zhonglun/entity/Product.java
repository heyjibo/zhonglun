package org.example.zhonglun.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "sales_volume", columnDefinition = "INT DEFAULT 0")
    private Integer salesVolume;

    private Integer status; // 1-上架, 0-下架

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;;
}
