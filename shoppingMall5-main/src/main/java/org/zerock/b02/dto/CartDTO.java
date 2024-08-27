package org.zerock.b02.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private String image;
    private Long productId;
    private String productName;
    private int quantity;
    private String price;


}
