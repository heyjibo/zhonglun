package org.example.zhonglun.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.ProductRequest;
import org.example.zhonglun.entity.Merchant;
import org.example.zhonglun.entity.Order;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.repository.MerchantRepository;
import org.example.zhonglun.repository.OrderRepository;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.service.LogisticsService;
import org.example.zhonglun.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Data // Lombok for ShipRequest DTO
class ShipRequest {
    private String courierCompanyCode;
    private String trackingNumber;
}

@Slf4j
@RestController
@RequestMapping("/api/merchant")
@PreAuthorize("hasRole('MERCHANT')") // 验证用户类型是否为商家
public class MerchantController {

    private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);
    private final OrderRepository orderRepository;
    private final LogisticsService logisticsService;
    private final ProductService productService;
    private final MerchantRepository merchantRepository; // 直接注入用于查询档案

    @Autowired
    public MerchantController(ProductService productService, MerchantRepository merchantRepository, OrderRepository orderRepository, LogisticsService logisticsService) {
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.logisticsService = logisticsService;
        this.merchantRepository = merchantRepository;
    }

    // --- 新增接口：商家档案管理 ---

    /**
     * 获取当前商家的店铺信息
     */
    @GetMapping("/profile")
    public ResponseEntity<Merchant> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return merchantRepository.findByUserId(userDetails.getId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AccessDeniedException("商家档案不存在"));
    }

    // --- 商品管理 (CRUD) ---

    /**
     * 新增一个商品
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        // [逻辑变更] 传入 userId，由 Service 层处理后续逻辑
        Product createdProduct = productService.createProduct(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * 更新名下的一个商品
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @Valid @RequestBody ProductRequest request,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product updatedProduct = productService.updateProduct(productId, request, userDetails.getId());
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * 删除名下的一个商品
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        productService.deleteProduct(productId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取名下所有的商品列表
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getMyProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Product> products = productService.getProductsByMerchantId(userDetails.getId());
        return ResponseEntity.ok(products);
    }

    /**
     * 获取名下指定ID的商品详情
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getMyProductById(@PathVariable Long productId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productService.getProductByIdAndMerchantId(productId, userDetails.getId());
        return ResponseEntity.ok(product);
    }

    /**
     * 上架一个商品
     * @param productId 商品ID
     */
    @PutMapping("/products/{productId}/publish")
    public ResponseEntity<Product> publishProduct(@PathVariable Long productId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productService.publishProduct(productId, userDetails.getId());
        return ResponseEntity.ok(product);
    }

    /**
     * 下架一个商品
     * @param productId 商品ID
     */
    @PutMapping("/products/{productId}/delist")
    public ResponseEntity<Product> delistProduct(@PathVariable Long productId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productService.delistProduct(productId, userDetails.getId());
        return ResponseEntity.ok(product);
    }

    @PostMapping("/orders/{orderId}/ship")
    // @PreAuthorize("hasRole('MERCHANT')") // 生产环境请务必加上权限控制
    public ResponseEntity<String> shipOrder(@PathVariable Long orderId, @RequestBody ShipRequest shipRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // 更新订单状态。您使用的是String, 我们这里用"SHIPPED"作为示例
        order.setStatus("SHIPPED");
        order.setCourierCompanyCode(shipRequest.getCourierCompanyCode());
        order.setTrackingNumber(shipRequest.getTrackingNumber());

        try {
            String kdnResponse = logisticsService.subscribeToTracking(
                    order.getCourierCompanyCode(),
                    order.getTrackingNumber()
            );
            logger.info("KDN Subscription Response for order {}: {}", orderId, kdnResponse);
        } catch (Exception e) {
            logger.error("Failed to subscribe to KDN for orderId: {}. Still proceeding with shipping status update.", orderId, e);
        }

        orderRepository.save(order);

        return ResponseEntity.ok("Order has been shipped and tracking subscription is initiated.");
    }
}
