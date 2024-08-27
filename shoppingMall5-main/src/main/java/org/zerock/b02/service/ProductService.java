package org.zerock.b02.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b02.domain.Product;
import org.zerock.b02.dto.ProductDTO;

import java.util.Optional;

public interface ProductService {
    Page<Product> getProducts(Pageable pageable);
    Page<ProductDTO> getProductList(Pageable pageable, String keyword);
    Page<ProductDTO> getProductsByCategory(String categoryId, String keyword, Pageable pageable);
    Optional<Product> findProductById(Long id);
    ProductDTO getProductById(Long id);


    void showProduct(ProductDTO productDTO);
    void addProduct(ProductDTO productDTO);
    void updateProduct(ProductDTO productDTO);
    void deleteProduct(Long id);
    void saveProduct(ProductDTO productDTO);

}
