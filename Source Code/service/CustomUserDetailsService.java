package com.example.union.service;

import com.example.union.entity.MemberEntity;
import com.example.union.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("UserDetailsService: 사용자 로드 시도, 이메일={}", email);

        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(email);

        if (byMemberEmail.isPresent()) {
            // 조회 결과가 있다 (해당 이메일을 가진 회원이 있다)
            MemberEntity memberEntity = byMemberEmail.get();

            // logger.info("UserDetailsService: DB에서 사용자 발견, 이메일={}", memberEntity.getMemberEmail());
            // logger.info("UserDetailsService: DB 저장 비밀번호 (해싱됨)={}", memberEntity.getMemberPassword());

            return new org.springframework.security.core.userdetails.User(
                    memberEntity.getMemberEmail(),
                    memberEntity.getMemberPassword(),
                    Collections.emptyList()
            );

        } else {
            // 조회 결과가 없다 (해당 이메일의 회원이 없다)
            logger.warn("UserDetailsService: 해당 이메일의 사용자 찾을 수 없음: {}", email);
            throw new UsernameNotFoundException("해당 이메일(" + email + ")의 사용자를 찾을 수 없습니다.");
        }
    }

}
