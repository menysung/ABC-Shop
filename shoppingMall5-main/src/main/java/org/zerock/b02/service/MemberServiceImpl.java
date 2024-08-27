package org.zerock.b02.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;
import org.zerock.b02.dto.MemberDTO;
import org.zerock.b02.dto.MemberJoinDTO;
import org.zerock.b02.exception.MidExistException;
import org.zerock.b02.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<MemberDTO> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(member -> MemberDTO.builder()
                .mid(member.getMid())
                .email(member.getEmail())
                .role(member.getRole())
                .build());
    }

    @Override
    public MemberDTO getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .map(member -> MemberDTO.builder()
                        .mid(member.getMid())
                        .email(member.getEmail())
                        .role(member.getRole())
                        .build())
                .orElse(null);
    }


    @Override
    public void deleteMember(String mid) {
        memberRepository.deleteById(mid);
    }

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        // 사용자 ID가 이미 존재하는지 확인하는 로직 추가
        if (memberRepository.existsById(memberJoinDTO.getMid())) {
            throw new MidExistException("Member ID already exists");
        }
        // 회원가입 로직
        Member member = Member.builder()
                .mid(memberJoinDTO.getMid())
                .mpw(passwordEncoder.encode(memberJoinDTO.getMpw())) // 비밀번호 암호화
                .email(memberJoinDTO.getEmail())
                .phone(memberJoinDTO.getPhone())
                .address(memberJoinDTO.getAddress())
                .build();

        memberRepository.save(member); // DB에 저장
    }

    @Override
    public Member findById(String mid) {
        log.info("Finding member by ID: " + mid);
        try {
            return memberRepository.findById(mid)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
        } catch (Exception e) {
            log.error("Error finding member by ID: " + mid, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateMember(Member member) {
        // 비밀번호 암호화 처리
        if (member.getMpw() != null && !member.getMpw().isEmpty()) {
            member.changePassword(passwordEncoder.encode(member.getMpw()));
        }
        memberRepository.save(member);
    }


}
