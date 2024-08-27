package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b02.dto.ProductDTO;
import org.zerock.b02.domain.Product;
import org.zerock.b02.repository.ProductRepository;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    //    @Override
//    public Page<ProductDTO> getProducts(Pageable pageable) {
//        return productRepository.findAll(pageable).map(this::entityToDto);
//    }

    @Override
    public void saveProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .slug(productDTO.getSlug())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice()) // BigDecimal 그대로 사용
                .categoryId(productDTO.getCategoryId())
                .image(productDTO.getImage())
                .build();

        productRepository.save(product);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::entityToDto)
                .orElse(null);
    }

    @Override
    public void showProduct(ProductDTO productDTO) {
        // ProductDTO를 사용하여 특정 작업을 수행
        System.out.println("ProductDTO: " + productDTO);
    }


    @Override
    public Page<ProductDTO> getProductList(Pageable pageable, String keyword) {
        return productRepository.findByKeyword(keyword, pageable).map(this::entityToDto);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(String categoryId, String keyword, Pageable pageable) {
        if (categoryId == null || categoryId.isEmpty()) {
            return productRepository.findByKeyword(keyword, pageable).map(this::entityToDto);
        } else {
            if (keyword == null || keyword.isEmpty()) {
                return productRepository.findByCategoryId(categoryId, pageable).map(this::entityToDto);
            } else {
                return productRepository.findByKeywordAndCategoryId(keyword, categoryId, pageable).map(this::entityToDto);
            }
        }
    }

    private ProductDTO entityToDto(Product product) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice()) // BigDecimal 그대로 반환
                .image(product.getImage())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .build();
    }

    @Override
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public void updateProduct(ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(productDTO.getId()).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(productDTO.getPrice()); // BigDecimal 그대로 사용
            existingProduct.setImage(productDTO.getImage());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setCategoryId(productDTO.getCategoryId());
            productRepository.save(existingProduct);
        }
    }

    @Override
    public void addProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice()) // BigDecimal 그대로 사용
                .image(productDTO.getImage())
                .description(productDTO.getDescription())
                .categoryId(productDTO.getCategoryId())
                .build();
        productRepository.save(product);
    }

    private ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .slug(product.getSlug())
                .description(product.getDescription())
                .categoryId(product.getCategoryId())
                .build();
    }
}
