package com.bmt.java_bmt.configurations;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationExceptionException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.getStatusCode().value());
        /*
        	- Mục đích:
        		+ Khai báo rằng response trả về cho client có định dạng là JSON.
        	- Chi tiết:
        		+ MediaType.APPLICATION_JSON_VALUE tương đương với chuỗi "application/json".
        */
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        APIResponse<?> apiResponse = APIResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        /*
        	- Mục đích:
        		+ Tạo một instance của ObjectMapper – đây là một class của thư viện Jackson,
        		dùng để chuyển đổi giữa Java Object và JSON.
        */
        ObjectMapper objectMapper = new ObjectMapper();
        /*
        	objectMapper.writeValueAsString(apiResponse) chuyển object apiResponse thành chuỗi JSON.
        	res.getWriter().write(...) ghi chuỗi đó vào response body.
        */
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
