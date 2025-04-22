## 서비스 및 컨트롤러 구현


비즈니스 로직을 처리하기 위한 UserService 클래스를 작성하였다. 로그인 시도 시 사용자 정보를 확인하는 메서드를 구현하였다.


```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
```


사용자 로그인 요청을 처리하기 위한 LoginController 클래스를 작성하였다. 로그인 폼을 보여주는 메서드와 POST 요청을 처리하는 메서드를 구현하였다.


```java
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userService.findUserByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return "redirect:/home";
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
}
```


## 로그인 페이지 구현


src/main/resources/templates에 login.html 파일을 생성하고, 로그인 폼을 작성하였다.


```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login</title>
</head>
<body>
    <h1>Login</h1>
    <form action="#" th:action="@{/login}" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
        <button type="submit">Login</button>
        <p th:if="${error}" th:text="${error}"></p>
    </form>
</body>
</html>
```