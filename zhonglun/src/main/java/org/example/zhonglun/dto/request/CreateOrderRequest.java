package org.example.zhonglun.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单的请求体
 */
@Data
public class CreateOrderRequest {

    /**
     * 将从购物车中结算的商品项
     */
    @NotEmpty(message = "结算的商品列表不能为空")
    private List<OrderItemRequest> items;

    /**
     * 用户选择的收货地址ID
     */
    @NotNull(message = "必须指定收货地址")
    private Long addressId;

    /**
     * 备注
     */
    private String remark;
}
