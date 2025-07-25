package org.example.zhonglun.service.impl;

import jakarta.persistence.criteria.Predicate;
import org.example.zhonglun.dto.response.ProductPublicResponse;
import org.example.zhonglun.entity.Product;
import org.example.zhonglun.exception.ResourceNotFoundException;
import org.example.zhonglun.repository.ProductRepository;
import org.example.zhonglun.service.PlatformService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformServiceImpl implements PlatformService {

    private final ProductRepository productRepository;

    @Autowired
    public PlatformServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductPublicResponse> listPublishedProducts(Pageable pageable, String keyword) {
        // 使用 Specification 动态构建查询条件
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 基础条件：必须是上架商品
            predicates.add(cb.equal(root.get("status"), 1));

            // 可选条件：如果关键词不为空，则进行模糊查询
            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        // 将 Page<Product> 转换为 Page<ProductPublicResponse>
        return productPage.map(this::convertToPublicResponse);
    }

    @Override
    public ProductPublicResponse getPublishedProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 核心安全检查：如果商品不是上架状态，也视为“未找到”
        if (product.getStatus() != 1) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        return convertToPublicResponse(product);
    }

    /**
     * 辅助方法：将 Product 实体转换为 ProductPublicResponse DTO
     */
    private ProductPublicResponse convertToPublicResponse(Product product) {
        ProductPublicResponse response = new ProductPublicResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }
}

