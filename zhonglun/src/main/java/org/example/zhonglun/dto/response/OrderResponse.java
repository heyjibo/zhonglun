package org.example.zhonglun.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.zhonglun.entity.Address;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private BigDecimal totalPrice;
    private Integer status; // 0-待付款, 1-待发货, 2-已发货, 3-已完成, 4-已取消
    private String remark;
    private LocalDateTime createTime;
    private Address shippingAddress;
    private List<OrderItemResponse> items;
}
