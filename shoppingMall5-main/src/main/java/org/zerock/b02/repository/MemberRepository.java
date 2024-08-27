package org.zerock.b02.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b02.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findById(String mid);


    //유저 id 로 유저와 권한을 가져옴. 이때 권한은 lazy 로딩이므로 바로 가져올 수 있도록 EntityGraph 사용함
    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(@Param("mid") String mid);


    @EntityGraph(attributePaths = "roleSet")
    List<Member> findByEmail(String email);

    //패스워드 업데이트
    @Modifying
    @Transactional
    @Query("update Member m set m.mpw =:mpw where m.mid = :mid ")
    void updatePassword(String mpw, String mid);

    @Query("select m from Member m where m.mid = :mid")
    Member findOneByMid(@Param("mid") String mid);



}
