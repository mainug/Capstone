## 프로젝트 초기 설정


IntelliJ IDEA를 사용하여 Spring Boot 프로젝트를 생성하였다. Gradle을 빌드 도구로 선택하고, 필요한 의존성(스프링 웹, 스프링 데이터 JPA, MySQL 드라이버 등)을 추가하였다.


기본 패키지 구조를 설정하고, 이후의 개발을 위해 controller, service, repository, model 패키지를 생성하였다.


## MySQL 데이터베이스 설정


MySQL에서 user_db라는 데이터베이스를 생성하고, users 테이블을 다음과 같은 구조로 생성하였다.


```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```


Spring Boot의 application.properties 파일에 MySQL 데이터베이스 연결 정보를 추가하였다.


```java
spring.datasource.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```