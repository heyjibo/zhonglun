package org.example.zhonglun.repository;

import org.example.zhonglun.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 商家查看自己的商品
    List<Product> findByMerchantId(Long merchantId);
    // 顾客浏览所有上架的商品
    List<Product> findByStatus(Integer status);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
