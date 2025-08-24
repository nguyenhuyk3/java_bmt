package com.bmt.java_bmt.exceptions;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bmt.java_bmt.dto.APIResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
/*
Đánh dấu class này là "bộ xử lý ngoại lệ toàn cục" (global exception handler) cho tất cả các controller trong app.
Spring sẽ tự động bắt các lỗi được ném ra trong controller và chuyển vào đây xử lý.
*/
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<APIResponse> handleRuntimeException(RuntimeException ex) {
        SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .forEach(a -> log.info("Authority: {}", a.getAuthority()));

        return ResponseEntity.badRequest()
                .body(APIResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<APIResponse> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        APIResponse res = new APIResponse();

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(APIResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<APIResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(APIResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> handleValidation(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        APIResponse.APIResponseBuilder<Void> responseBuilder =
                APIResponse.<Void>builder().code(errorCode.getCode()).message(errorCode.getMessage());

        if (errors.size() == 1) {
            responseBuilder.error(errors);
        } else if (errors.size() > 1) {
            responseBuilder.errors(errors);
        }

        return ResponseEntity.badRequest().body(responseBuilder.build());
    }
}
