package org.zerock.b02.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.b02.dto.MemberDTO;
import org.zerock.b02.dto.OrderDTO;
import org.zerock.b02.dto.ProductDTO;
import org.zerock.b02.service.MemberService;
import org.zerock.b02.service.OrderService;
import org.zerock.b02.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final ProductService productService;
    private final MemberService memberService;
    private final OrderService orderService;

    // 사용자 관리
    @GetMapping("/manageUsers")
    public String manageUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("mid").ascending());
        var pageResponse = memberService.getAllMembers(pageable);

        if (page > pageResponse.getTotalPages()) {
            page = pageResponse.getTotalPages();
            pageable = PageRequest.of(page-1, size, Sort.by("mid").ascending());
            pageResponse = memberService.getAllMembers(pageable);
        }

        model.addAttribute("users", pageResponse.getContent());
        model.addAttribute("pageResponse", pageResponse);
        return "admin/manageUsers"; // 사용자 관리 페이지로 이동
    }

    @GetMapping("/editUser")
    public String editUser(@RequestParam String memberId, Model model) {
        MemberDTO memberDTO = memberService.getMemberById(memberId);
        if (memberDTO == null) {
            return "redirect:/admin/manageUsers"; // 사용자 존재하지 않으면 목록 페이지로 리다이렉트
        }
        model.addAttribute("member", memberDTO);
        return "admin/editUser"; // 사용자 수정 페이지로 이동
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String memberId) {
        try {
            memberService.deleteMember(memberId); // 사용자 삭제 로직
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // 오류 페이지로 리다이렉트
        }
        return "redirect:/admin/manageUsers"; // 삭제 후 사용자 목록 페이지로 리다이렉트
    }

    // 상품 관리
    @GetMapping("/productAdd")
    public String showAddProductForm() {
        return "admin/productAdd"; // 상품 추가 폼 페이지
    }

    @PostMapping("/productAdd")
    public String addProduct(@RequestParam("name") String name,
                             @RequestParam("price") String price,
                             @RequestParam("files") MultipartFile image,
                             @RequestParam("description") String description,
                             @RequestParam("categoryId") String categoryId) {
        try {
            // Save the image file and get the path
            String imagePath = saveFile(image);

            BigDecimal priceValue = new BigDecimal(price.replaceAll(",", ""));

            // Create a ProductDTO object with the String price
            ProductDTO productDTO = ProductDTO.builder()
                    .name(name)
                    .price(priceValue) // BigDecimal로 직접 설정
                    .image(imagePath)
                    .description(description)
                    .categoryId(categoryId)
                    .build();

            // Save the product
            productService.addProduct(productDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Handle exceptions
        }

        return "redirect:/product/productNewList"; // Redirect to the new products page
    }

    @GetMapping("/productEdit")
    public String editProduct(@RequestParam Long productId, Model model) {
        ProductDTO productDTO = productService.getProductById(productId);
        if (productDTO == null) {
            return "redirect:/admin/manageProducts"; // 상품이 존재하지 않으면 목록 페이지로 리다이렉트
        }
        model.addAttribute("product", productDTO);
        return "admin/productEdit"; // 상품 수정 페이지로 이동
    }

    @PostMapping("/productUpdate")
    public String updateProduct(ProductDTO productDTO) {
        // 기존 상품 정보를 가져옴
        ProductDTO existingProduct = productService.getProductById(productDTO.getId());

        if (existingProduct != null) {
            // 기존 이미지 URL을 유지
            if (productDTO.getImage() == null || productDTO.getImage().isEmpty()) {
                productDTO.setImage(existingProduct.getImage());
            }
        }
        productService.updateProduct(productDTO); // 상품 수정 로직
        return "redirect:/product/productDetail?id=" + productDTO.getId() + "&updated=true"; // 수정 후 상품 상세 페이지로 리다이렉트
    }

    @GetMapping("/productDelete")
    public String deleteProduct(@RequestParam("productId") Long productId) {
        try {
            productService.deleteProduct(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // 오류 페이지로 리다이렉트
        }
        return "redirect:/product/productAllList"; // 삭제 후 상품 목록 페이지로 리다이렉트
    }

    // 주문 관리 페이지
    @GetMapping("/orderList")
    public String viewOrders(Model model) {
        List<OrderDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orderList";
    }

    // 주문 상태 업데이트 API
    @PostMapping("/updateOrderStatus")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        try {
            // 주문 상태를 업데이트
            orderService.updateOrderStatus(orderId, status);

            // 업데이트된 주문 상태를 조회
            OrderDTO updatedOrder = orderService.getOrderById(orderId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "업데이트되었습니다.");
            response.put("status", updatedOrder != null ? updatedOrder.getStatus() : "정보 없음");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> response = new HashMap<>();
            response.put("message", "오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    // 파일 저장 메소드
    private String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + "_" + originalFilename;

            File directory = new File("C:\\upload");
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리가 없으면 생성
            }

            File serverFile = new File(directory.getAbsolutePath() + File.separator + filename);
            file.transferTo(serverFile);
            return filename; // 저장된 파일 이름을 반환
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
