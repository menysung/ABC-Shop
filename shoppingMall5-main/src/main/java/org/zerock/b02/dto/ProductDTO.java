package org.zerock.b02.dto;

import lombok.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
    private String slug;
    private String description;
    private String categoryId;
    private String brand;

    // 가격을 쉼표로 구분된 문자열로 반환하는 메서드
    public String getFormattedPrice() {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);
        return currencyFormat.format(price);
    }




}


