package org.zerock.b02.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private int quantity;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    private String image;


    @PrePersist
    private void prePersist() {
        if (product != null) {
            this.price = product.getPrice(); // Product에서 가져온 가격을 BigDecimal로 설정
            this.image = product.getImage();
        }
    }




}
