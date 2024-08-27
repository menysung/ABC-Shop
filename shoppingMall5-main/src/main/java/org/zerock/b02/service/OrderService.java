package org.zerock.b02.service;

import org.zerock.b02.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    void updateOrderStatus(Long orderId, String status);
    OrderDTO createOrder(String address, String paymentMethod, String cardNumber, String expiryDate, String cvv);
    void createOrder(String memberName);  // 수정 필요
    List<OrderDTO> getOrders(String memberName);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long orderId);  // 메서드 추가
}
