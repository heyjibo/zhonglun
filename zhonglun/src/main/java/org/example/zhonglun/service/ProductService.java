package org.example.zhonglun.service;

public interface ProductService {
    // ... 原有方法

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
