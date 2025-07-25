package org.example.zhonglun.service;

import org.example.zhonglun.dto.request.ProductRequest;
import org.example.zhonglun.entity.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductRequest request, Long userId);
    Product updateProduct(Long productId, ProductRequest request, Long userId);
    void deleteProduct(Long productId, Long userId);
    Product getProductByIdAndMerchantId(Long productId, Long userId);
    List<Product> getProductsByMerchantId(Long userId);
    Product publishProduct(Long productId, Long userId);
    Product delistProduct(Long productId, Long userId);
    /**
     * 在应用启动或商品更新时，将商品库存加载到 Redis
     * @param productId 商品ID
     */
    void loadStockToRedis(Long productId);

    /**
     * 使用 Lua 脚本原子性扣减库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 是否扣减成功
     */
    boolean deductStock(Long productId, Integer quantity);

    /**
     * 原子性增加库存 (用于取消订单等场景)
     * @param productId 商品ID
     * @param quantity 增加数量
     */
    void increaseStock(Long productId, Integer quantity);
}
