package org.zerock.b02.repository;

import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMember(Member member); // Member를 기준으로 주문 조회
}
