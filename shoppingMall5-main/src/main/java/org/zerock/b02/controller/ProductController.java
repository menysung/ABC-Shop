package org.zerock.b02.controller;

import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.b02.domain.Product;
import org.zerock.b02.dto.PageRequestDTO;
import org.zerock.b02.dto.PageResponseDTO;
import org.zerock.b02.dto.ProductDTO;
import org.zerock.b02.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ProductController {

    @Value("${upload.path:C:\\upload}")
    private String uploadPath;

    private final ProductService productService;

    @GetMapping("/home")
    public String home(@RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Product> productPage = productService.getProducts(pageable);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("pageResponse", productPage);
        return "home"; // Thymeleaf 템플릿 파일명
    }


    @GetMapping("/product/show/{id}")
    public String showProduct(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOptional = productService.findProductById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "productDetail";
        } else {
            return "error";
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/api/products")
    public Page<ProductDTO> getProductsByCategory(@RequestParam String categoryId,
                                                  @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        return productService.getProductsByCategory(categoryId, keyword, pageable);
    }

    @GetMapping("/product/productSaleList")
    public String getProductSaleList(@RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "categoryId", required = false) String categoryId,
                                     Model model) {
        if (page <= 0) {
            page = 1;
        }
        if (keyword == null) {
            keyword = "";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<ProductDTO> productPage = productService.getProductsByCategory(categoryId, keyword, pageable);

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .categoryId(categoryId)
                .build();

        PageResponseDTO<ProductDTO> pageResponseDTO = PageResponseDTO.<ProductDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(productPage.getContent())
                .total((int) productPage.getTotalElements())
                .build();

        model.addAttribute("products", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponse", pageResponseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        return "product/productSaleList";
    }

    @GetMapping("/product/productNewList")
    public String getProductNewList(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "categoryId", required = false) String categoryId,
                                    Model model) {
        if (page <= 0) {
            page = 1;
        }
        if (keyword == null) {
            keyword = "";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<ProductDTO> productPage = productService.getProductsByCategory(categoryId, keyword, pageable);

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .categoryId(categoryId)
                .build();

        PageResponseDTO<ProductDTO> pageResponseDTO = PageResponseDTO.<ProductDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(productPage.getContent())
                .total((int) productPage.getTotalElements())
                .build();

        model.addAttribute("products", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponse", pageResponseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        return "product/productNewList";
    }

    @GetMapping("/product/productAllList")
    public String getProductAllList(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "categoryId", required = false) String categoryId,
                                    Model model) {
        if (page <= 0) {
            page = 1;
        }
        if (keyword == null) {
            keyword = "";
        }

        Pageable pageable = PageRequest.of(page-1 , size, Sort.by(Sort.Order.desc("createdAt")));
        Page<ProductDTO> productPage = productService.getProductsByCategory(categoryId, keyword, pageable);

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .categoryId(categoryId)
                .build();

        PageResponseDTO<ProductDTO> pageResponseDTO = PageResponseDTO.<ProductDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(productPage.getContent())
                .total((int) productPage.getTotalElements())
                .build();

        model.addAttribute("products", pageResponseDTO.getDtoList());
        model.addAttribute("pageResponse", pageResponseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        return "product/productAllList";
    }

    @GetMapping("/product/productDetail")
    public String getProductDetail(@RequestParam("id") Long id,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   Model model) {
        try {
            ProductDTO productDTO = productService.getProductById(id);

            if (productDTO == null) {
                return "error";
            }
            model.addAttribute("product", productDTO);
            model.addAttribute("page", page);
            model.addAttribute("size", size);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return "product/productDetail";
    }
}
