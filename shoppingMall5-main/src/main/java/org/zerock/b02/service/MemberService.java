package org.zerock.b02.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b02.domain.Member;
import org.zerock.b02.dto.MemberDTO;
import org.zerock.b02.dto.MemberJoinDTO;
import org.zerock.b02.exception.MidExistException;


import java.util.List;

public interface MemberService {
    Page<MemberDTO> getAllMembers(Pageable pageable);

    MemberDTO getMemberById(String memberId);

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;

    Member findById(String mid);

    void updateMember(Member member);

    void deleteMember(String mid);
}

