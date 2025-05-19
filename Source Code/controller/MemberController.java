package com.example.union.controller;

import com.example.union.dto.MemberDTO;
import com.example.union.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    // 생성자 주입
    private final MemberService memberService;
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class); // Logger 추가

    // 회원가입 페이지 출력 요청
    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO,
                       @RequestParam("password") String rawPassword,
                       Model model) {
        // logger.info("MemberController.save 메서드 호출됨");
        // logger.info("초기 memberDTO = {}", memberDTO); // password 필드는 null일 것입니다.
        // logger.info("rawPassword (from form name='password') = {}", rawPassword); // 폼에서 받은 비밀번호 값

        // @ModelAttribute로 바인딩되지 않은 password 값을 DTO에 수동으로 설정
        memberDTO.setMemberPassword(rawPassword); // <-- DTO에 password 값 설정 (MemberDTO에 setMemberPassword 필요)

        // logger.info("password 설정 후 memberDTO = {}", memberDTO); // 이제 password 필드에 값이 있습니다.

        try {
            memberService.save(memberDTO); // DTO 객체를 Service로 전달
            // 회원가입 성공 후 이동할 페이지 (예: 로그인 페이지 또는 회원가입 완료 페이지)
            return "redirect:/member/login"; // 또는 "/member/savesuccess" 등
        } catch (Exception e) { // save 메서드 실행 중 예외 발생 시
            logger.error("회원가입 중 오류 발생", e);
            // 오류 메시지를 모델에 담아 회원가입 폼으로 돌아갈 수 있습니다.
            model.addAttribute("errorMessage", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "save"; // 회원가입 폼 뷰 이름
        }
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/main") // /main 경로의 GET 요청을 처리
    public String mainPage() {
        // 이 메서드는 로그인 성공 후 Spring Security에 의해 호출됩니다.
        // 여기서는 "main" 이라는 뷰 이름을 반환합니다.
        return "main"; // templates 폴더의 main.html (또는 main.jsp 등)을 찾게 됩니다.
    }

    @GetMapping("/member/list")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model 사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/member/{id}")
    public String findById(@PathVariable Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "detail";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId();
    }

    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        memberService.deleteById(id);
        return "redirect:/member/";
    }

    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

}
