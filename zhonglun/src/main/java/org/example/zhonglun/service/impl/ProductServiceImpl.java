package org.example.zhonglun.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.ProductRequest;
import org.example.zhonglun.entity.Merchant;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.exception.ResourceNotFoundException;
import org.example.zhonglun.repository.MerchantRepository;
import org.example.zhonglun.repository.ProductRepository;
import org.example.zhonglun.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MerchantRepository merchantRepository;


    private DefaultRedisScript<Long> deductStockScript;
    private DefaultRedisScript<Long> increaseStockScript;

    // Redis 中库存的 Key 前缀
    private static final String STOCK_KEY_PREFIX = "product:stock:";

    public ProductServiceImpl(ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate, MerchantRepository merchantRepository) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.merchantRepository = merchantRepository;
    }

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
                Collections.singletonList(STOCK_KEY_PREFIX + productId),
                quantity
        );

        return Long.valueOf(1L).equals(result);
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

    @Override
    public Product createProduct(ProductRequest request, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);

        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        product.setMerchantId(merchant.getId());
        product.setStatus(0); // 默认下架
        product.setSalesVolume(0); // 默认销量为0

        log.info("用户ID: {} (商家ID: {}) 创建新商品: {}", userId, merchant.getId(), product.getName());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, ProductRequest request, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        Product existingProduct = getProductAndCheckOwnership(productId, merchant.getId());

        BeanUtils.copyProperties(request, existingProduct, "id", "merchantId", "status", "salesVolume", "createTime");

        log.info("用户ID: {} (商家ID: {}) 更新商品ID: {}", userId, merchant.getId(), productId);
        return productRepository.save(existingProduct);
    }

    // [新增] 实现上架逻辑
    @Override
    @Transactional
    public Product publishProduct(Long productId, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        Product product = getProductAndCheckOwnership(productId, merchant.getId());
        product.setStatus(1); // 1 = 上架
        log.info("用户ID: {} (商家ID: {}) 上架商品ID: {}", userId, merchant.getId(), productId);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product delistProduct(Long productId, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        Product product = getProductAndCheckOwnership(productId, merchant.getId());
        product.setStatus(0); // 0 = 下架
        log.info("用户ID: {} (商家ID: {}) 下架商品ID: {}", userId, merchant.getId(), productId);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        Product productToDelete = getProductAndCheckOwnership(productId, merchant.getId());
        productRepository.delete(productToDelete);
        log.info("用户ID: {} (商家ID: {}) 删除商品ID: {}", userId, merchant.getId(), productId);
    }

    @Override
    public Product getProductByIdAndMerchantId(Long productId, Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        return getProductAndCheckOwnership(productId, merchant.getId());
    }

    @Override
    public List<Product> getProductsByMerchantId(Long userId) {
        Merchant merchant = getMerchantProfileByUserId(userId);
        return productRepository.findByMerchantId(merchant.getId());
    }

    private Merchant getMerchantProfileByUserId(Long userId) {
        return merchantRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("安全检查失败：无法找到用户ID: {} 对应的商家档案", userId);
                    return new AccessDeniedException("当前用户不是认证商家或商家档案不存在");
                });
    }


    private Product getProductAndCheckOwnership(Long productId, Long merchantId) {
        // 1. 先根据ID查找商品是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 2. 检查商品的 merchantId 是否与当前登录商家的 merchantId 匹配
        if (!Objects.equals(product.getMerchantId(), merchantId)) {
            log.warn("安全警告: 商家ID {} 试图操作不属于自己的商品ID {}", merchantId, productId);
            throw new AccessDeniedException("您无权操作此商品");
        }
        return product;
    }
}
