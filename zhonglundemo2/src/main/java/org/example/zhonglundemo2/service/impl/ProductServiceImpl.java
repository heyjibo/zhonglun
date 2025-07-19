package org.example.zhonglundemo2.service.impl;

import org.example.zhonglundemo2.dto.ProductDTO;
import org.example.zhonglundemo2.entity.Merchant;
import org.example.zhonglundemo2.entity.Product;
import org.example.zhonglundemo2.exception.ResourceNotFoundException;
import org.example.zhonglundemo2.repository.MerchantRepository;
import org.example.zhonglundemo2.repository.ProductRepository;
import org.example.zhonglundemo2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final MerchantRepository merchantRepository;

    private final ProductRepository productRepository;

    @Override
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getAllActiveProducts(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setActive(productDTO.isActive());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setViewCount(product.getViewCount() + 1);
            productRepository.save(product);
        });
    }

    @Override
    public Page<ProductDTO> getProductsByMerchant(Long merchantId, Pageable pageable) {
        // 验证商家是否存在
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + merchantId));

        // 查询该商家的所有商品（包括上架和下架状态）
        Page<Product> products = productRepository.findByMerchant(merchant, pageable);

        // 转换为DTO并返回
        return products.map(this::convertToDTO);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        // 验证商家是否存在
        Merchant merchant = merchantRepository.findById(productDTO.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + productDTO.getMerchantId()));


        // 转换DTO为实体对象
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setMerchant(merchant);
        product.setActive(true); // 新商品默认上架
        product.setViewCount(0); // 初始浏览量为0

        // 保存商品
        Product savedProduct = productRepository.save(product);

        // 转换为DTO并返回
        return convertToDTO(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product", key = "#id")
    public ProductDTO updateProductStatus(Long id, boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(active);
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setViewCount(product.getViewCount());
        dto.setMerchantId(product.getMerchant().getId());
        dto.setActive(product.isActive());
        return dto;
    }
}