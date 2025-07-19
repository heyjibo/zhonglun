package org.example.zhonglundemo2.service;

import org.example.zhonglundemo2.dto.ProductDTO;
import org.example.zhonglundemo2.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDTO> getAllActiveProducts(Pageable pageable);
    Page<ProductDTO> getProductsByMerchant(Long merchantId, Pageable pageable);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProductStatus(Long id, boolean active);
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void incrementViewCount(Long productId);
}
