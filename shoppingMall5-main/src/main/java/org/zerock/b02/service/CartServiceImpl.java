package org.zerock.b02.service;

import jakarta.servlet.http.HttpSession;
import org.zerock.b02.domain.Cart;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.Product;
import org.zerock.b02.dto.CartDTO;
import org.zerock.b02.repository.CartRepository;
import org.zerock.b02.repository.MemberRepository;
import org.zerock.b02.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpSession session;


    @Override
    public void addToCart(Long productId, int quantity, String membername) {
        Member member = memberRepository.findById(membername)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByMemberMidAndProductId(member.getMid(), productId)
                .orElse(null);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = Cart.builder()
                    .member(member)
                    .product(product)
                    .quantity(quantity)
                    .build();
        }

        cartRepository.save(cart);

        // 세션에 장바구니 정보 저장
        List<CartDTO> cartItems = getCart(membername);
        session.setAttribute("cartItems", cartItems);
    }

    @Override
    public List<CartDTO> getCart(String membername) {
        Member member = memberRepository.findById(membername)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 통화 형식 인스턴스 생성
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.KOREA);

        // Cart 목록을 조회 및 DTO로 변환
        return cartRepository.findByMemberMid(member.getMid()).stream()
                .map(cart -> CartDTO.builder()
                        .productId(cart.getProduct().getId())
                        .productName(cart.getProduct().getName())
                        .quantity(cart.getQuantity())
                        .price(currencyFormat.format(cart.getPrice())) // BigDecimal로 포맷
                        .image(cart.getProduct().getImage())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public void updateCart(Long productId, int quantity, String membername) {
        // membername으로 Member 객체를 조회
        Member member = memberRepository.findById(membername)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // Cart 객체를 조회 및 업데이트
        Cart cart = cartRepository.findByMemberMidAndProductId(member.getMid(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void deleteCart(Long productId, String membername) {
        Member member = memberRepository.findById(membername)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Cart cart = cartRepository.findByMemberMidAndProductId(member.getMid(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartRepository.delete(cart);
    }

    //홈화면 카트 갯수 표시
    @Override
    public int getCartItemCount(String membername) {
        Member member = memberRepository.findById(membername)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return cartRepository.findByMemberMid(member.getMid()).stream()
                .mapToInt(Cart::getQuantity)
                .sum();
    }


}