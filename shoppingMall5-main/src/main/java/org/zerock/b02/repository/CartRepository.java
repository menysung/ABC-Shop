package org.zerock.b02.repository;

import org.springframework.stereotype.Repository;
import org.zerock.b02.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByMemberMidAndProductId(String memberMid, Long productId);
    List<Cart> findByMemberMid(String memberMid); // 사용자별 장바구니 항목 조회

}
