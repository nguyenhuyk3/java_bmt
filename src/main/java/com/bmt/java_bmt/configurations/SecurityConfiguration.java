package com.bmt.java_bmt.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.bmt.java_bmt.helpers.constants.Others;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Configuration
@EnableWebSecurity
/*
@Configuration
- Mục đích:
	+ Đánh dấu class hiện tại là class cấu hình (configuration class) trong Spring.
	+ Tương đương với việc thay thế file XML cấu hình (applicationContext.xml).
- Ý nghĩa:
	+ Spring sẽ quét và khởi tạo các @Bean trong class này để đưa vào Spring Container (IoC container).
	+ Ví dụ: bạn viết @Bean public PasswordEncoder passwordEncoder(), Spring sẽ gọi method đó và quản lý bean trả về.
@EnableWebSecurity
- Mục đích:
	+ Bật Spring Security cho ứng dụng web.
	+ Import cấu hình mặc định của Spring Security.
- Ý nghĩa:
	+ Khi bạn thêm annotation này, Spring sẽ tự động tạo ra một SecurityFilterChain mặc định,
trừ khi bạn định nghĩa @Bean SecurityFilterChain của riêng bạn (custom).
	+ Nếu không thêm @EnableWebSecurity, Spring Boot vẫn bật security mặc định,
nhưng bạn không thể cấu hình chi tiết được.
*/
@EnableMethodSecurity(prePostEnabled = true)
/*
	- @EnableMethodSecurity là annotation của Spring Security dùng để bật bảo mật ở cấp độ method
— tức là cho phép sử dụng các annotation như:
		+ @PreAuthorize
		+ @PostAuthorize
		+ @Secured
		+ @RolesAllowed
	- Ý nghĩa:
		+ Annotation này bật AOP proxy để can thiệp vào các method (controller/service)
	và kiểm tra quyền truy cập.
		+ Nếu bạn không thêm @EnableMethodSecurity, thì @PreAuthorize
	và các annotation tương tự sẽ không hoạt động, dù bạn có cấu hình Security đúng.
*/
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {
    String[] POST_PUBLIC_ENDPOINTS = {"/auth/register/**", "/auth/login/**", "/auth/forgot-password/**", "/film/**"};
    String[] PUT_PUBLIC_ENDPOINTS = {"/auth/forgot-password/**"};
    String[] GET_PUBLIC_ENDPOINTS = {"/user/**"};
    String[] POST_MANAGER_PRIVATE_ENDPOINTS = {"/film/**"};

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Autowired
    @NonFinal
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(req -> req.requestMatchers(HttpMethod.POST, POST_MANAGER_PRIVATE_ENDPOINTS)
                .hasRole(Others.MANAGER)
                .requestMatchers(HttpMethod.GET, GET_PUBLIC_ENDPOINTS)
                .hasAnyRole(Others.CUSTOMER, Others.MANAGER)
                .requestMatchers(HttpMethod.POST, POST_PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.PUT, PUT_PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));

        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
