package com.bmt.java_bmt.services.authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bmt.java_bmt.dto.others.TokenPair;
import com.bmt.java_bmt.dto.requests.authentication.login.LoginRequest;
import com.bmt.java_bmt.dto.requests.authentication.login.RefreshAccessTokenRequest;
import com.bmt.java_bmt.entities.PersonalInformation;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Sex;
import com.bmt.java_bmt.entities.enums.Source;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.implementations.authentication.LoginImpl;
import com.bmt.java_bmt.repositories.IUserRepository;

import lombok.extern.slf4j.Slf4j;

/*
@SpringBootTest: chạy toàn bộ Spring context (giống lúc app chạy thật).
@Testcontainers: bật hỗ trợ Testcontainers trong JUnit 5, đảm bảo container được start/stop khi chạy test.
@AutoConfigureMockMvc: tự động cấu hình MockMvc, dùng để test controller mà không cần khởi động server thật.
*/
@SpringBootTest
@Testcontainers
// @AutoConfigureMockMvc
@Slf4j
/*
	Annotation @TestPropertySource("classpath:test.properties"): có tác dụng nạp
và áp dụng các thuộc tính cấu hình (properties) từ một file được chỉ định,
ghi đè lên các cấu hình mặc định (như application.properties) chỉ trong phạm vi của bài test đó.
*/
// @TestPropertySource("classpath:test.properties")
public class LoginServiceTest {
    /*
    Khởi động MySQL container
    @Container: JUnit sẽ tự động khởi động container khi test bắt đầu và dừng container khi test kết thúc.
    */
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    /*
    @DynamicPropertySource: dùng để inject các property động từ container vào Spring Boot context.
    @DynamicPropertySource → báo cho Spring rằng method này sẽ cung cấp các property động.
    DynamicPropertyRegistry registry → là nơi bạn đăng ký các property.
    */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private LoginImpl loginService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IJwtTokenService jwtTokenService;

    /*
    GIVEN → Chuẩn bị bối cảnh, dữ liệu, state trước khi hành động.
    WHEN → Hành động chính mà bạn muốn test (gọi service, gọi API, chạy method).
    THEN → Xác minh kết quả (assert, verify).
    */

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        PersonalInformation personalInformation = PersonalInformation.builder()
                .fullName("Nguyen Quoc Huy")
                .dateOfBirth(LocalDate.of(1998, 5, 15))
                .sex(Sex.MALE)
                .avatarUrl("https://example.com/avatar.png")
                .build();
        User user = User.builder()
                .email("huy@example.com")
                .password(passwordEncoder.encode("1234567"))
                .role(Role.CUSTOMER)
                .source(Source.APP)
                .personalInformation(personalInformation)
                .build();

        userRepository.save(user);
    }

    @Test
    void shouldloginSuccess() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("huy@example.com")
                .password("1234567")
                .build();
        // WHEN
        TokenPair tokenPair = loginService.login(request);
        // THEN
        assertNotNull(tokenPair);
        assertNotNull(tokenPair.getAccessToken());
        assertNotNull(tokenPair.getRefreshToken());

        log.info("Token Pair = " + tokenPair.toString());
    }

    @Test
    void shouldThrowException_whenEmailNotFound() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("notFound@example.com")
                .password("1234567")
                .build();
        // WHEN & THEN
        /*
        assertThrows: Nó dùng để kiểm tra một đoạn code có ném ra exception mong đợi hay không.
        Cú pháp: <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable)
        */
        AppException exception = assertThrows(AppException.class, () -> loginService.login(request));

        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void shouldThrowException_whenPasswordIncorrecat() {
        // GIVEN
        LoginRequest request = LoginRequest.builder()
                .email("huy@example.com")
                .password("wrongpassword")
                .build();
        // WHEN & THEN
        AppException exception = assertThrows(AppException.class, () -> loginService.login(request));

        assertEquals(ErrorCode.PASSWORD_INCORRECT, exception.getErrorCode());
    }

    @Test
    void shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        // GIVEN
        LoginRequest loginRequest = LoginRequest.builder()
                .email("huy@example.com")
                .password("1234567")
                .build();
        TokenPair tokenPair = loginService.login(loginRequest);
        String refreshToken = tokenPair.getRefreshToken();
        RefreshAccessTokenRequest refreshAccessTokenRequest =
                RefreshAccessTokenRequest.builder().token(refreshToken).build();
        // WHEN
        String actualAccessToken = loginService.refreshAccessToken(refreshAccessTokenRequest);
        // THEN
        assertThat(actualAccessToken.isEmpty());
    }
}
