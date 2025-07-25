package org.example.zhonglun.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 用于平台公开展示的商品信息，隐藏了库存、状态等内部数据。
 */
@Data
public class ProductPublicResponse {
    private Long id;
    private Long merchantId; // 可以保留，用于链接到商家店铺
    private String name;
    private String description;
    private BigDecimal price;
    private Integer salesVolume; // 销量是重要的公开信息
}
