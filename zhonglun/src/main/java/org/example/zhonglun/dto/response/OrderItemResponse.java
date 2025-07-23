package org.example.zhonglun.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productImageUrl; // 假设商品有图片
    private Integer quantity;
    private BigDecimal unitPrice;
}
