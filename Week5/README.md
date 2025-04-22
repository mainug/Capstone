## 사용자 모델 및 리포지토리 구현


model 패키지에 User 클래스를 생성하고, JPA 어노테이션을 사용하여 필드를 매핑하였다.


```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;

    // getters and setters
}
```


repository 패키지에 UserRepository 인터페이스를 생성하여 JPA의 CRUD 기능을 활용하였다.


```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```


