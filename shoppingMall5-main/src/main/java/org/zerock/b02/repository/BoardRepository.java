package org.zerock.b02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b02.domain.Board;
import org.zerock.b02.repository.search.BoardSearch;

import java.util.Optional;

//JpaRepository 상속후 <테이블클래스명, ID 타입>
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    //CRUD 자동 생성
    //커스텀 메소드 만들기
    //Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable);
    //@Query("select b from Board b where b.title like concat('%',:keyword,'%')")
    //Page<Board> findKeyword(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(Long bno);


    @Query("SELECT COUNT(r) FROM Reply r WHERE r.board.bno = :bno")
    int countRepliesByBoardBno(@Param("bno") Long bno);
}
