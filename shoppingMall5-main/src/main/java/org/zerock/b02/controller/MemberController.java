package org.zerock.b02.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b02.domain.Member;
import org.zerock.b02.dto.MemberJoinDTO;
import org.zerock.b02.exception.MidExistException;
import org.zerock.b02.service.MemberService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, String> deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 로그인한 사용자의 ID

        Map<String, String> result = new HashMap<>();

        try {
            memberService.deleteMember(mid);
            // 로그아웃 처리
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            result.put("deleteResult", "success");
            result.put("redirectUrl", "/member/login"); // 리디렉션 URL
        } catch (Exception e) {
            log.error("Error deleting member: " + mid, e);
            result.put("deleteResult", "failure");
        }

        return result;
    }

    @GetMapping("/login")
    public void loginGET(String error, String logout) {
        log.info("login GET .....");
        log.info("로그아웃 : " + logout);
        if (logout != null) {
            log.info("유저 로그아웃....");
        }
    }

    @GetMapping("/join")
    public void joinGET() {
        log.info("join GET .....");
    }

    @PostMapping("/join")
    public String joinPOST(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes) {
        log.info("join POST .....");
        log.info(memberJoinDTO);

        try {
            memberService.join(memberJoinDTO);
        } catch (MidExistException e) {
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/join";
        }

        redirectAttributes.addFlashAttribute("result", "success");
        return "redirect:/member/login"; // 회원가입 후 로그인 페이지로
    }


    @GetMapping("/myAccount")
    public String myAccount(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 인증되지 않은 경우 로그인 페이지로
        }
        String mid = authentication.getName(); // 로그인한 사용자의 ID
        log.info("Logged in user ID: " + mid);

        Member member = memberService.findById(mid);

        // DTO로 변환
        MemberJoinDTO memberDTO = MemberJoinDTO.builder()
                .mid(member.getMid())
                .email(member.getEmail())
                .mpw(member.getMpw())
                .phone(member.getPhone())
                .address(member.getAddress())
                .del(member.isDel())
                .social(member.isSocial())
                .build();

        model.addAttribute("member", memberDTO);

        return "member/myAccount";
    }

    @PostMapping("/myAccount")
    @ResponseBody
    public Map<String, String> updateAccount(@ModelAttribute("member") MemberJoinDTO memberDTO, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mid = authentication.getName(); // 로그인한 사용자의 ID

        // 기존 회원 정보를 가져옴
        Member existingMember = memberService.findById(mid);

        // 비밀번호는 암호화가 필요하므로, 변경하지 않는 경우 비밀번호 유지
        Member updatedMember = Member.builder()
                .mid(existingMember.getMid())
                .email(memberDTO.getEmail())
                .mpw(memberDTO.getMpw() != null && !memberDTO.getMpw().isEmpty() ? memberDTO.getMpw() : existingMember.getMpw())
                .del(memberDTO.isDel())
                .social(memberDTO.isSocial())
                .build();

        memberService.updateMember(updatedMember);

        Map<String, String> response = new HashMap<>();
        response.put("updateResult", "success");

        return response;
    }

    @GetMapping("/notice")
    public String notice() {
        return "member/notice";
    }

    @GetMapping("/faq")
    public String faq() {
        return "member/faq";
    }






}
