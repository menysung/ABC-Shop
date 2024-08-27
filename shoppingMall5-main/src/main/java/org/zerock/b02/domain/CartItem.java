package org.zerock.b02.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItem {
    private Long productId;       // 상품 ID
    private String productName;   // 상품명
    private int quantity;         // 수량
    private double price;         // 단가
    private String image;         // 이미지 URL
}
