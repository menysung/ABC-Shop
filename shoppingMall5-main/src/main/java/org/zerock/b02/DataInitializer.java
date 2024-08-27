package org.zerock.b02;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.zerock.b02.domain.Member;
import org.zerock.b02.domain.MemberRole;
import org.zerock.b02.repository.MemberRepository;

import java.util.Set;

@Component
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (memberRepository.findById("admin").isEmpty()) {
                Member admin = Member.builder()
                        .mid("admin")
                        .mpw(passwordEncoder.encode("admin123"))
                        .email("admin@example.com")
                        .roleSet(Set.of(MemberRole.ADMIN))
                        .build();
                memberRepository.save(admin);
            }
        };
    }
}
