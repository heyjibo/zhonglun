package org.example.zhonglundemo2.dto;

import java.math.BigDecimal;

public interface ProductSalesProjection {
    Long getProductId();
    String getProductName();
    Integer getQuantitySold();
    BigDecimal getTotalRevenue();
}