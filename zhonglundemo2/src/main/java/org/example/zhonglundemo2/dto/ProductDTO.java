package org.example.zhonglundemo2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer viewCount;
    private Long merchantId;
    private boolean active;
}