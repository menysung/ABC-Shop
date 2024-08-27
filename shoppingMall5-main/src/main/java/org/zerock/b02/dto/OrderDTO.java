package org.zerock.b02.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDTO {
    private Long id; // 주문 ID
    private LocalDateTime orderDate; // 주문 날짜
    private BigDecimal totalPrice; // 총액
    private List<OrderItemDTO> items; // 주문 아이템 목록
    private String orderNumber; // 주문 번호
    private String status; // 상태 추가
    private String memberId; // 주문한 멤버 ID 추가



    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", items=" + items +
                ", orderNumber='" + orderNumber + '\'' +
                '}';
    }
}
