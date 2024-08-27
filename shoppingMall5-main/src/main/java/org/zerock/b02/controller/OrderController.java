package org.zerock.b02.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.zerock.b02.domain.CartItem;
import org.zerock.b02.dto.CartDTO;
import org.zerock.b02.dto.OrderDTO;
import org.zerock.b02.service.CartService;
import org.zerock.b02.service.OrderService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/member")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpSession session;

    private BigDecimal parsePrice(String priceString) {
        try {
            // 가격 문자열에서 통화 기호 제거
            String cleanString = priceString.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleanString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }


    @GetMapping("/confirm")
    public String confirmOrder(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String memberName = authentication.getName();

        // 세션에서 장바구니 정보 가져오기
        List<CartDTO> cartItems = (List<CartDTO>) session.getAttribute("cartItems");

        if (cartItems == null) {
            // 장바구니가 비어 있는 경우 처리
            cartItems = cartService.getCart(memberName); // CartService에서 장바구니 가져오기
            session.setAttribute("cartItems", cartItems);
        }

        // 총 금액 계산
        BigDecimal totalAmount = cartItems.stream()
                .map(cartDTO -> parsePrice(cartDTO.getPrice())) // price 필드를 사용하여 parsePrice 호출
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal 객체들 간의 합 계산

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.KOREA);
        String formattedTotalAmount = currencyFormatter.format(totalAmount);

        model.addAttribute("cart", cartItems);
        model.addAttribute("totalAmount", formattedTotalAmount);
        model.addAttribute("shippingCost", "무료");

        return "member/confirm";
    }

    @PostMapping("/createOrder")
    public String createOrder(Authentication authentication) {
        String membername = authentication.getName();
        orderService.createOrder(membername);
        return "redirect:/member/myOrders";
    }


    @PostMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/member/myOrders";
    }

    @PostMapping("/order/payment")
    public String completeOrder(@RequestParam("address") String address,
                                @RequestParam("paymentMethod") String paymentMethod,
                                @RequestParam("cardNumber") String cardNumber,
                                @RequestParam("expiryDate") String expiryDate,
                                @RequestParam("cvv") String cvv,
                                Model model) {
        try {
            OrderDTO orderDTO = orderService.createOrder(address, paymentMethod, cardNumber, expiryDate, cvv);
            model.addAttribute("orderNumber", orderDTO.getOrderNumber()); // 주문 번호 추가
            return "member/success"; // 주문 완료 페이지
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // 오류 페이지
        }
    }

    @GetMapping("/myOrders")
    public String getOrders(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String memberName = authentication.getName();
        List<OrderDTO> orders = orderService.getOrders(memberName);

        model.addAttribute("orders", orders);

        return "member/myOrders";
    }


}