package org.zerock.b02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b02.domain.Order;
import org.zerock.b02.dto.OrderDTO;
import org.zerock.b02.dto.OrderItemDTO;
import org.zerock.b02.repository.CartRepository;
import org.zerock.b02.repository.OrderRepository;
import org.zerock.b02.repository.MemberRepository;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.Cart;
import org.zerock.b02.domain.OrderItem;
import org.zerock.b02.security.dto.MemberSecurityDTO;

import java.util.Set;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 현재 로그인한 사용자의 정보를 가져오는 메서드
    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
            MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();
            return memberRepository.findById(principal.getMid())
                    .orElseThrow(() -> new RuntimeException("Member not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }


    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setStatus(status);  // 주문 상태 업데이트
        orderRepository.save(order);  // 변경된 상태를 데이터베이스에 저장
    }

    @Override
    public OrderDTO createOrder(String address, String paymentMethod, String cardNumber, String expiryDate, String cvv) {

        Member member = getCurrentMember();

        // 장바구니 아이템 조회
        List<Cart> cartItems = cartRepository.findByMemberMid(member.getMid());

        System.out.println("Cart items for member " + member.getMid() + ": " + cartItems);


        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 총 금액 계산
        BigDecimal totalPrice = cartItems.stream()
                .map(cart -> cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 주문 번호 생성
        String orderNumber = "ORD" + System.currentTimeMillis();

        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status("NEW")
                .orderNumber(orderNumber)
                .build();

        // OrderItem 객체 생성
        Set<OrderItem> orderItems = cartItems.stream()
                .map(cart -> OrderItem.builder()
                        .product(cart.getProduct())
                        .quantity(cart.getQuantity())
                        .price(cart.getPrice())
                        .order(order)
                        .build())
                .collect(Collectors.toSet());

        // 주문에 아이템 설정
        order.setItems(orderItems);

        // 주문 저장
        orderRepository.save(order);

        // 장바구니 아이템 삭제
        cartRepository.deleteAll(cartItems);

        // OrderDTO 생성 및 반환
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())  // 주문 번호 설정
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }


    @Override
    public void createOrder(String memberName) {
        // 회원 조회
        Member member = memberRepository.findById(memberName)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 장바구니 아이템 조회
        List<Cart> cartItems = cartRepository.findByMemberMid(member.getMid());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 총 금액 계산
        BigDecimal totalPrice = cartItems.stream()
                .map(cart -> cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 주문 번호 생성
        String orderNumber = "ORD" + System.currentTimeMillis();

        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .status("NEW")
                .orderNumber(orderNumber)
                .items(cartItems.stream()
                        .map(cart -> OrderItem.builder()
                                .product(cart.getProduct())
                                .quantity(cart.getQuantity())
                                .price(cart.getPrice())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        // 주문 저장
        orderRepository.save(order);

        // 장바구니 아이템 삭제
        cartRepository.deleteAll(cartItems);

        // 추가적인 로직 (예: 결제 처리 등)
    }

    @Override
    public List<OrderDTO> getOrders(String memberName) {
        return orderRepository.findByMember(memberRepository.findById(memberName)
                        .orElseThrow(() -> new RuntimeException("Member not found"))).stream()
                .map(order -> OrderDTO.builder()
                        .id(order.getId())
                        .orderNumber(order.getOrderNumber())
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .items(order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> OrderDTO.builder()
                        .id(order.getId())
                        .memberId(order.getMember().getMid())
                        .orderNumber(order.getOrderNumber())
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .items(order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .status(order.getStatus())  // 주문 상태
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public OrderDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> OrderDTO.builder()
                        .id(order.getId())
                        .memberId(order.getMember().getMid())
                        .orderDate(order.getOrderDate())
                        .items(order.getItems().stream()
                                .map(item -> OrderItemDTO.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .collect(Collectors.toList()))
                        .status(order.getStatus())  // 주문 상태
                        .build())
                .orElse(null);
    }

}
