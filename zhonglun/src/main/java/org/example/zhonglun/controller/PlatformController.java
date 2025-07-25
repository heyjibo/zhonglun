package org.example.zhonglun.controller;

import org.example.zhonglun.dto.response.ProductPublicResponse;
import org.example.zhonglun.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform") // 使用独立的 "platform" 路径
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    /**
     * 公开接口：获取已上架的商品列表（分页、可搜索）
     * @param pageable Spring自动注入的分页参数 (e.g., /api/platform/products?page=0&size=10&sort=salesVolume,desc)
     * @param keyword  可选的搜索关键词
     * @return 分页后的商品数据
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductPublicResponse>> listProducts(
            @PageableDefault(size = 10, sort = "createTime") Pageable pageable,
            @RequestParam(required = false) String keyword) {
        Page<ProductPublicResponse> products = platformService.listPublishedProducts(pageable, keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * 公开接口：获取单个已上架商品的详情
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductPublicResponse> getProductDetails(@PathVariable Long productId) {
        ProductPublicResponse product = platformService.getPublishedProductById(productId);
        return ResponseEntity.ok(product);
    }
}
