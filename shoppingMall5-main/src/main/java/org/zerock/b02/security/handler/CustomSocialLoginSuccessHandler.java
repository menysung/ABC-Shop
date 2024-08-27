package org.zerock.b02.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.b02.security.dto.MemberSecurityDTO;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final PasswordEncoder passwordEncoder;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("------------------------------------------------------");
        log.info("Custom Social Login Success Handler");
        log.info(authentication.getPrincipal());
        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();

        // 소셜 로그인 사용자로 인증된 경우
        if (memberSecurityDTO.isSocial()) {
            // 소셜 로그인 사용자의 권한을 설정하거나 세션에 추가 속성 설정
            request.getSession().setAttribute("isSocialUser", true);
        }

        String encodedPW = memberSecurityDTO.getMpw();
        //처음 가입시 소셜로 가입하고 임시로 저장한 비번 1234가 같으면 modify 페이지로 간다
        if(memberSecurityDTO.isSocial() && passwordEncoder.matches("1234", encodedPW)) {
            log.info("비밀번호를 재설정해주세요.");
            log.info("Redirect to Member Modify");
            response.sendRedirect("/member/modify");
            return;
        } else {
            response.sendRedirect("/board/list"); //그 외의 경우
        }
    }


}