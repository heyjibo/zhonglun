package org.example.zhonglundemo2.dto;

import java.math.BigDecimal;

public class ProductSalesDTO {
    private final Long productId;
    private final String productName;
    private final Integer quantitySold;
    private final BigDecimal totalRevenue;

    public ProductSalesDTO(Long productId, String productName, Integer quantitySold, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}