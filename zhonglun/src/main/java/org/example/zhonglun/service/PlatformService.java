package org.example.zhonglun.service;

import org.example.zhonglun.dto.response.ProductPublicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformService {

    /**
     * 分页查询已上架的商品列表，并支持按名称搜索。
     * @param pageable 分页参数
     * @param keyword 搜索关键词（商品名称）
     * @return 分页后的商品DTO列表
     */
    Page<ProductPublicResponse> listPublishedProducts(Pageable pageable, String keyword);

    /**
     * 根据ID获取一个已上架的商品详情。
     * @param productId 商品ID
     * @return 商品详情DTO
     */
    ProductPublicResponse getPublishedProductById(Long productId);
}
