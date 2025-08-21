package com.bmt.java_bmt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST_BODY(10001, "Thân yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    EMAIL_IS_IN_REGISTRATION_PROCESS(20001, "Email của bạn đang trong quá trình đăng kí", HttpStatus.CONFLICT),
    EMAIL_EXISTED(20002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_TEMPLATE_ERROR(20003, "Lỗi khi đọc template email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SENDING_ERROR(20004, "Lỗi khi gửi email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_IS_NOT_REGISTRATION_PROCESS(20005, "Email không trong quá trình đăng kí", HttpStatus.BAD_REQUEST),
    OTP_DONT_MATCH(20006, "Mã OTP của bạn không khớp", HttpStatus.BAD_REQUEST),
    EMAIL_IS_NOT_IN_COMPLETION_REGISTRATION(20007, "Email không trong trạng thái hoàn thành đăng kí", HttpStatus.BAD_REQUEST),

    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Lỗi không xác định", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1007, "Bạn không có quyền truy cập vào tài nguyên", HttpStatus.FORBIDDEN),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}