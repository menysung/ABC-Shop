package org.zerock.b02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b02.domain.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 게시글 번호로 검색하므로 쿼리문을 JPA query 로 만들어야함
    // 쿼리문이 객체 형식 r = reply, : = 입력되는 변수
    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    void deleteByBoard_Bno(Long bno);
}
