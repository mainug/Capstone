Spring Boot 프로젝트 생성

(Spring Initializr 쓰면 편함) 필요한 의존성 추가

Spring Web, Spring Data JPA, Spring Security, Thymeleaf (또는 다른 템플릿 엔진), H2 Database (테스트용) 포함

application.properties 파일에 데이터베이스 설정 추가

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

회원 정보를 저장할 엔티티 클래스 생성

```java
import javax.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    // getters and setters
}
```

JPA를 사용해서 데이터베이스와 상호작용할 리포지토리 인터페이스 생성

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

회원가입 로직 처리할 서비스 클래스 생성

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) {
        // 비밀번호 암호화 로직 추가함 (BCryptPasswordEncoder 사용)
        userRepository.save(user);
    }
}
```

회원가입 요청 처리할 컨트롤러 생성

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html로 이동함
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login"; // 회원가입 후 로그인 페이지로 이동함
    }
}
```

Thymeleaf 쓰고 register.html 파일 생성

예시 html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>회원가입</title>
</head>
<body>
    <h1>회원가입</h1>
    <form action="#" th:action="@{/register}" th:object="${user}" method="post">
        <label for="username">사용자 이름:</label>
        <input type="text" th:field="*{username}" required /><br/>

        <label for="password">비밀번호:</label>
        <input type="password" th:field="*{password}" required /><br/>

        <label for="email">이메일:</label>
        <input type="email" th:field="*{email}" required /><br/>

        <button type="submit">가입하기</button>
    </form>
</body>
</html>
```

비밀번호 안전하게 저장하려고 BCryptPasswordEncoder 사용

UserService에서 비밀번호 암호화 로직 추가
