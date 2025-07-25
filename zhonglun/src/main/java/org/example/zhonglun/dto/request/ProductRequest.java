package org.example.zhonglun.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 用于接收创建和更新商品请求的数据。
 * 注意：不包含 status 和 salesVolume，这些由业务逻辑控制。
 */
@Data
public class ProductRequest {

    @NotEmpty(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String name;

    @Size(max = 500, message = "商品描述长度不能超过500个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "商品库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
}

