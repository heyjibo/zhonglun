package org.example.zhonglun.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.zhonglun.dto.request.ProductRequest;
import org.example.zhonglun.entity.Merchant;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.repository.MerchantRepository;
import org.example.zhonglun.security.CustomUserDetails;
import org.example.zhonglun.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/merchant")
@PreAuthorize("hasRole('MERCHANT')") // 验证用户类型是否为商家
public class MerchantController {

    private final ProductService productService;
    private final MerchantRepository merchantRepository; // 直接注入用于查询档案

    @Autowired
    public MerchantController(ProductService productService, MerchantRepository merchantRepository) {
        this.productService = productService;
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
}
