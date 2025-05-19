package com.example.union.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 필요시

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 만약 API 서버와 프론트엔드가 동일 도메인이 아니라면 CORS와 함께 추가 설정 필요

                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                            .requestMatchers("/**").permitAll()

                    // 만약 API 경로가 있다면, API 경로는 인증 필요
                            .requestMatchers("/api/**").authenticated();

                    // 서버에서 렌더링되는 로그인/회원가입 페이지 경로도 당연히 허용
                    // .requestMatchers("/member/saveform", "/member/save", "/member/login").permitAll()
                    // 위 경로는 "/**" permitAll()에 포함되므로 별도로 명시하지 않아도 됩니다.
                })
                // 폼 기반 로그인을 설정합니다.
                .formLogin(formLogin ->
                        formLogin
                                // 로그인 페이지 URL
                                .loginPage("/member/login")
                                // 로그인 인증 처리를 할 URL (POST 요청)
                                .loginProcessingUrl("/member/login")
                                // 로그인 성공 시 이동할 기본 페이지를 설정합니다.
                                .defaultSuccessUrl("/", true) // 항상 /main으로 이동
                                // 로그인 실패 시 이동할 페이지를 설정합니다.
                                .failureUrl("/member/login?error")
                                // 로그인 관련 URL들은 인증 없이 접근 허용 (loginPage, loginProcessingUrl)
                                .permitAll() // <-- 이 설정으로 /member/login (GET, POST)는 permitAll 됩니다. 위에 중복 설정 가능
                )
                // 로그아웃 설정을 합니다.
                .logout(logout ->
                                logout
                                        // 로그아웃을 처리할 URL (GET 또는 POST)
                                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // GET /member/logout 요청으로 로그아웃 처리
                                        // 로그아웃 성공 시 이동할 페이지
                                        .logoutSuccessUrl("/member/login?logout") // 예: 로그아웃 후 로그인 페이지로 이동
                                        // 세션 무효화
                                        .invalidateHttpSession(true)
                        // 쿠키 삭제 (필요시 추가)
                        // .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

}
