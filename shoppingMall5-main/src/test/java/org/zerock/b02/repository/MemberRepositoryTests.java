package org.zerock.b02.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers() {
        //총 100의 유저 생성
        IntStream.rangeClosed(1,100).forEach(i -> {
            Member member = Member.builder()
                    .mid("member"+i)
                    .mpw(passwordEncoder.encode("1111")) //mpw 동일하게 1111 입력했지만 다른 문자열 생성 됨
                    .email("email"+i+"@aaa.bbb")
                    .build();
            member.addRole(MemberRole.USER);

            if (i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        });
    }

    //멤버 조회 테스트
    @Test
    public void testRead() {
        Optional<Member> result = memberRepository.getWithRoles("member99");

        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());

        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));
    }

    @Commit
    @Test
    public void testUpdate() {

        String mid ="menysung94@naver.com"; //소셜 로그인으로 추가된 사용자. DB에 존재하는 이메일
        String mpw = passwordEncoder.encode("5678"); //수정할 패스워드

        memberRepository.updatePassword(mpw,mid);

    }





}
