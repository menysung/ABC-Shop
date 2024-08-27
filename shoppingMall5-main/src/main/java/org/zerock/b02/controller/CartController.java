package org.zerock.b02.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b02.dto.CartDTO;
import org.zerock.b02.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/member")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestParam Long productId,
                                       @RequestParam int quantity,
                                       Authentication authentication) {
        try {
            String membername = authentication.getName(); // 현재 로그인된 사용자 이름 가져오기
            cartService.addToCart(productId, quantity, membername);
            return ResponseEntity.ok("장바구니에 추가되었습니다!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니 추가 실패");
        }
    }

    @PostMapping("/updateCart")
    public ResponseEntity<?> updateCart(@RequestParam Long productId,
                                        @RequestParam int quantity,
                                        Authentication authentication) {
        try {
            String membername = authentication.getName(); // 현재 로그인된 사용자 이름 가져오기
            cartService.updateCart(productId, quantity, membername);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니 업데이트 실패");
        }
    }

    @PostMapping("/deleteCart")
    public ResponseEntity<?> deleteCart(@RequestParam Long productId, Authentication authentication) {
        try {
            String membername = authentication.getName(); // 현재 로그인된 사용자 이름 가져오기
            cartService.deleteCart(productId, membername);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("장바구니 삭제 실패");
        }
    }


    @GetMapping("/myCart")
    public String getCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }

        String memberName = authentication.getName();
        List<CartDTO> cartItems = cartService.getCart(memberName);
        model.addAttribute("cart", cartItems);

        return "member/myCart";
    }

    @GetMapping("/cartItemCount")
    @ResponseBody
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        try {
            String membername = authentication.getName();
            int itemCount = cartService.getCartItemCount(membername);
            return ResponseEntity.ok(itemCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}