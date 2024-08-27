package org.zerock.b02.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b02.dto.PageRequestDTO;
import org.zerock.b02.dto.PageResponseDTO;
import org.zerock.b02.dto.ReplyDTO;
import org.zerock.b02.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(@Valid @RequestBody ReplyDTO replyDTO,
                                     BindingResult bindingResult) throws BindException {
        log.info("Registering new reply: " + replyDTO);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        Map<String,Long> map = new HashMap<>();

        Long rno = replyService.register(replyDTO);
        map.put("rno", rno);

        return map;
    }

    //get 으로 게시물의 댓글 목록가져옴
    @GetMapping("/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                             PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ReplyDTO> responseDTO =
                replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    //특정 댓글조회
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno) {
        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(@PathVariable("rno") Long rno,
                                    @RequestBody ReplyDTO replyDTO,
                                    Authentication authentication) {

        // 현재 댓글과 작성자를 가져옴
        ReplyDTO existingReplyDTO = replyService.read(rno);
        if (existingReplyDTO == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }

        // 현재 사용자와 댓글 작성자를 비교하고 관리자의 권한을 확인
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!currentUsername.equals(existingReplyDTO.getReplyer()) && !isAdmin) {
            throw new SecurityException("권한이 없습니다.");
        }

        replyDTO.setRno(rno);
        replyService.modify(replyDTO);
        Map<String,Long> map = new HashMap<>();
        map.put("rno", rno);
        return map;
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{rno}")
    public Map<String,Long> delete(@PathVariable("rno") Long rno,
                                   Authentication authentication) {

        // 현재 댓글과 작성자를 가져옴
        ReplyDTO existingReplyDTO = replyService.read(rno);
        if (existingReplyDTO == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }

        // 현재 사용자와 댓글 작성자를 비교하고 관리자의 권한을 확인
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!currentUsername.equals(existingReplyDTO.getReplyer()) && !isAdmin) {
            throw new SecurityException("권한이 없습니다.");
        }

        replyService.remove(rno);
        Map<String,Long> map = new HashMap<>();
        map.put("rno", rno);
        return map;
    }
}
