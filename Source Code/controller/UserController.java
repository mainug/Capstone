package com.example.union.controller;

// src/main/java/your/package/controller/UserController.java (예시 경로)

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user") // /api 경로로 시작하도록 하여 백엔드 API임을 명확히 합니다.
public class UserController {

    @GetMapping("/status")
    public ResponseEntity<?> getUserStatus(Authentication authentication) {
        // Spring Security는 로그인된 사용자 정보를 Authentication 객체에 담아줍니다.
        if (authentication != null && authentication.isAuthenticated()) {
            // 'anonymousUser'는 로그인되지 않은 경우의 기본 주체 이름입니다.
            // 실제 사용자 이름 또는 아이디가 필요하면 authentication.getName() 등을 사용하세요.
            if (!"anonymousUser".equals(authentication.getName())) {
                // 로그인된 사용자에게는 사용자 정보(예: 이름, 역할 등)를 담아 보낼 수 있습니다.
                // 여기서는 간단히 로그인 상태임을 알립니다.
                return ResponseEntity.ok().body(true); // 또는 사용자 정보 객체 반환
            }
        }
        // 로그인되지 않은 상태
        return ResponseEntity.ok().body(false); // 또는 401 Unauthorized 응답
    }

    // 필요하다면 로그인된 사용자의 상세 정보를 제공하는 엔드포인트를 추가할 수 있습니다.
    // @GetMapping("/info")
    // public ResponseEntity<?> getUserInfo(Authentication authentication) {
    //     if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
    //         // Custom UserDetails를 사용한다면 해당 객체에서 정보를 추출합니다.
    //         // 예: UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    //         // return ResponseEntity.ok().body(userDetails);
    //         return ResponseEntity.ok().body(authentication.getPrincipal()); // 예시
    //     }
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    // }
}

