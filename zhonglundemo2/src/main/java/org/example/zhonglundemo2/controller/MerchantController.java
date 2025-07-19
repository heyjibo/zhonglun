package org.example.zhonglundemo2.controller;

import org.example.zhonglundemo2.dto.ProductDTO;
import org.example.zhonglundemo2.dto.SalesDataDTO;
import org.example.zhonglundemo2.service.ProductService;
import org.example.zhonglundemo2.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {
    private final ProductService productService;
    private final SalesService salesService;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getMerchantProducts(
            @RequestParam Long merchantId,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByMerchant(merchantId, pageable));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productDTO));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @PatchMapping("/products/{id}/status")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(productService.updateProductStatus(id, active));
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesDataDTO> getSalesData(@RequestParam Long merchantId) {
        return ResponseEntity.ok(salesService.getSalesDataByMerchant(merchantId));
    }
}