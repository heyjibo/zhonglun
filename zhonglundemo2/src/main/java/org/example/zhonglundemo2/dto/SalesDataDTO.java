package org.example.zhonglundemo2.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesDataDTO {
    private BigDecimal totalSales;
    private Integer totalOrders;
    private Integer totalProductsSold;

    private List<ProductSalesDTO> topSellingProducts;
    public SalesDataDTO(BigDecimal totalSales, int totalOrders, int totalProductsSold) {
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.totalProductsSold = totalProductsSold;
    }
}