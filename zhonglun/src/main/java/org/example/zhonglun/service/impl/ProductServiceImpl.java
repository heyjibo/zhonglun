package org.example.zhonglun.service.impl;

import jakarta.annotation.PostConstruct;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.exception.ResourceNotFoundException; // 假设有这个自定义异常
import org.example.zhonglun.repository.ProductRepository;
import org.example.zhonglun.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<Long> deductStockScript;
    private DefaultRedisScript<Long> increaseStockScript;

    // Redis 中库存的 Key 前缀
    private static final String STOCK_KEY_PREFIX = "product:stock:";

    @PostConstruct
    public void init() {
        // 初始化 Lua 脚本
        deductStockScript = new DefaultRedisScript<>();
        deductStockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/deduct_stock.lua")));
        deductStockScript.setResultType(Long.class);

        increaseStockScript = new DefaultRedisScript<>();
        increaseStockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/increase_stock.lua")));
        increaseStockScript.setResultType(Long.class);

        // 项目启动时，预热所有商品库存到 Redis
        preloadAllStockToRedis();
    }

    // ... 其他 ProductService 方法

    @Override
    public void loadStockToRedis(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + productId, product.getStock());
    }

    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        // 执行 Lua 脚本，保证原子性
        Long result = redisTemplate.execute(
                deductStockScript,
                Collections.singletonList(STOCK_KEY_PREFIX + productId), // KEYS[1]
                quantity // ARGV[1]
        );
        // 脚本返回 1 表示成功，0 表示库存不足
        return result != null && result == 1L;
    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {
        redisTemplate.execute(
                increaseStockScript,
                Collections.singletonList(STOCK_KEY_PREFIX + productId), // KEYS[1]
                quantity // ARGV[1]
        );
    }

    private void preloadAllStockToRedis() {
        System.out.println("开始预加载所有商品库存到 Redis...");
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + product.getId(), product.getStock());
        }
        System.out.println("库存预加载完成，共处理 " + products.size() + " 个商品。");
    }
}
