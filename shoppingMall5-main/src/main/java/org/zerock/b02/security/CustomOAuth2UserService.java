package org.zerock.b02.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;
import org.zerock.b02.repository.MemberRepository;
import org.zerock.b02.security.dto.MemberSecurityDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Oauth2 user Request");
        log.info(userRequest);

        log.info("==================== Oauth2 User ======================");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("Name: " + clientName);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;
        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }
        log.info("============================================");
        log.info("Email: " + email);
        log.info("============================================");
        return generateDTO(email, paramMap);
    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        List<Member> result = memberRepository.findByEmail(email);
        //데이터베이스에 해당 이메일 사용자가 없으면
        if (result.isEmpty()) {
            //회원을 추가하기
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1234"))
                    .email(email)
                    .social(true) //소셜 로그인 이므로 true
                    .build();
            member.addRole(MemberRole.USER); //유저 권한을 준다
            memberRepository.save(member);
            //DTO로 리턴하기
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email,"1234",email,false, true,
                            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);
            return memberSecurityDTO;
        } else {
            Member member = result.get(0); //첫번째 요소 가져옴
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getMpw(),
                            member.getEmail(),
                            member.isDel(),
                            member.isSocial(),
                            member.getRoleSet()
                                    .stream().map(role->new SimpleGrantedAuthority("ROLE"+role.name()))
                                    .collect(Collectors.toList())
                    );
            return memberSecurityDTO;
        }
    }

    private String getKakaoEmail(Map<String, Object> paramMap) {
        Object value = paramMap.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap)value;
        String email = (String)accountMap.get("email");
        return email;
    }
}