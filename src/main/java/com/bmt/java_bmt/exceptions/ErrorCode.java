package com.bmt.java_bmt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Email
    EMAIL_IS_IN_REGISTRATION_PROCESS(20001, "Email của bạn đang trong quá trình đăng kí", HttpStatus.CONFLICT),
    EMAIL_EXISTED(20002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_TEMPLATE_ERROR(20003, "Lỗi khi đọc template email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SENDING_ERROR(20004, "Lỗi khi gửi email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_IS_NOT_REGISTRATION_PROCESS(20005, "Email không trong quá trình đăng kí", HttpStatus.BAD_REQUEST),
    OTP_DONT_MATCH(20006, "Mã OTP của bạn không khớp", HttpStatus.BAD_REQUEST),
    EMAIL_IS_NOT_IN_REGISTRATION_COMPLETION(
            20007, "Email không trong trạng thái hoàn thành đăng kí", HttpStatus.BAD_REQUEST),
    EMAIL_IS_IN_REGISTRATION_COMPLETION(20008, "Email đang trong trạng thái hoàn thành đăng kí", HttpStatus.CONFLICT),
    EMAIL_NOT_FOUND(20013, "Email không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_IS_IN_FORGOT_PASSWORD_PROCESS(20009, "Email đang trong quá trình đổi mật khẩu", HttpStatus.CONFLICT),
    EMAIL_IS_IN_FORGOT_PASSWORD_COMPLETION(
            20010, "Email đang trong trạng thái hoàn thành quá trình quên mật khẩu", HttpStatus.CONFLICT),
    EMAIL_IS_NOT_IN_FORGOT_PASSWORD_COMPLETION(
            20011, "Email không trong trạng thái hoàn quá trình quên mật khẩu", HttpStatus.BAD_REQUEST),
    PASSWORD_DONT_MATCH(20006, "Mật khẩu không khớp", HttpStatus.BAD_REQUEST),
    UPDATE_PASSWORD_FAILED(20006, "Cập nhập mật khẩu thất bại", HttpStatus.INTERNAL_SERVER_ERROR),

    // Token
    FAILED_TO_CREATE_ACCESS_TOKEN(21009, "Lỗi tạo access token", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_CREATE_REFRESH_TOKEN(21010, "Lỗi tạo refresh token", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_CREATE_TOKEN_PAIR(21011, "Lỗi tạo pair token", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_TYPE_DONT_MATCH(21012, "Token không đúng loại", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED(21013, "Access token đã hết hạn", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID(21014, "Access token không hợp lệ", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_UNSUPPORTED(21015, "Access token không được hỗ trợ", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EMPTY(21016, "Access token rỗng", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(21017, "Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(21018, "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_UNSUPPORTED(21019, "Refresh token không được hỗ trợ", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EMPTY(21020, "Refresh token rỗng", HttpStatus.BAD_REQUEST),
    CANNOT_EXTRACT_USER_ID(21021, "Không thể trích xuất ID của người dùng từ token", HttpStatus.BAD_REQUEST),
    CANNOT_EXTRACT_TOKEN_EXPIRATION(21022, "Không thể trích xuất thời gian hết hạn từ token", HttpStatus.BAD_REQUEST),

    // User
    USER_ID_DOESNT_EXIST(22001, "User không tồn tại", HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(22002, "Mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    PROFESSIONAL_ID_DOESNT_EXIST(22003, "Người làm phim không tồn tại", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(23001, "Bạn chưa được xác thực. Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(23002, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN),

    // Product
    INVALID_PRODUCT_TYPE(24001, "Loại sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    FILM_NOT_FOUND(24002, "Phim không tồn tại", HttpStatus.NOT_FOUND),
    CLOUDINARY_DELETE_FILE_FAILED(24003, "Xóa file ở Cloudinary thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FAB_NOT_FOUND(24004, "Thức ăn hay đồ uống không tồn tại", HttpStatus.NOT_FOUND),

    // Cinema
    AUDITORIUM_NOT_FOUND(25001, "Phòng chiếu không tồn tại", HttpStatus.NOT_FOUND),
    CANNOT_ADD_SHOWTIME_FOR_TODAY(25002, "Không thể thêm phim cho ngày hôm này", HttpStatus.BAD_REQUEST),
    SHOWTIME_OVERFLOW_TO_NEXT_DAY(25003, "Suất chiếu vượt quá 00:00, không thể thêm mới", HttpStatus.BAD_REQUEST),
    SHOWTIME_NOT_FOUND(25004, "Không tìm thấy xuất chiếu", HttpStatus.NOT_FOUND),
    RELEASE_SHOWTIME_FAILED(
            25005, "Không thể thay đổi trạng thái phát hành của suất chiếu", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_ENOUGH_SHOWTIME_SEATS(25006, "Tạo ghế cho suất chiếu không đủ", HttpStatus.INTERNAL_SERVER_ERROR),
    SHOWTIME_IS_IN_PAST(25007, "Suất chiếu đã ở quá khứ", HttpStatus.BAD_REQUEST),
    SEAT_NOT_FOUND(25008, "Không tìm thấy ghế", HttpStatus.NOT_FOUND),
    SHOWTIME_SEATS_NOT_FOUND_IN_CACHE(
            25009, "Ghế cho suất chiếu không được tìm thấy trong bộ nhớ đệm", HttpStatus.NOT_FOUND),
    SEAT_NOT_FOUND_IN_SHOWTIME(25010, "Ghế không được tìm thấy trong xuất chiếu", HttpStatus.NOT_FOUND),
    SHOWTIME_IS_NOT_RELEASED(25011, "Suất chiếu chưa được công chiếu", HttpStatus.BAD_REQUEST),
    UPDATE_SEAT_FOR_SHOWTIME_FAILED(
            25012, "Cập nhập trạng thái ghế cho suất chiếu thất bại", HttpStatus.INTERNAL_SERVER_ERROR),

    // Elasticsearch
    ELASTICSEARCH_INDEX_NOT_FOUND(25001, "Không tìm thấy index trong Elasticsearch", HttpStatus.NOT_FOUND),
    ELASTICSEARCH_SEARCH_FAILED(25002, "Tìm kiếm phim thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    ELASTICSEARCH_SEARCH_IO_EXCEPTION(25003, "Lỗi khi kết nối Elasticsearch", HttpStatus.INTERNAL_SERVER_ERROR),

    // Momo payment
    MOMO_SIGNATURE_FAILED(26001, "Không thể tạo chữ ký giao dịch MoMo", HttpStatus.INTERNAL_SERVER_ERROR),
    MOMO_INVALID_SIGNATURE(26002, "Chữ ký MoMo không hợp lệ", HttpStatus.BAD_REQUEST),
    MOMO_REQUEST_FAILED(26003, "Gọi API MoMo thất bại", HttpStatus.BAD_GATEWAY),
    MOMO_RESPONSE_ERROR(26004, "Giao dịch bị từ chối bởi MoMo", HttpStatus.BAD_REQUEST),
    MOMO_AMOUNT_INVALID(26005, "Số tiền thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    MOMO_ORDER_INFO_INVALID(26006, "Thông tin đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    MOMO_IPN_INVALID(26007, "Dữ liệu IPN từ MoMo không hợp lệ", HttpStatus.BAD_REQUEST),

    // Order
    ORDER_HAS_EXPIRED(27001, "Đơn đặt hàng đã hết hạn", HttpStatus.NOT_FOUND),
    TOTAL_DO_NOT_MATCH(27002, "Tổng tiền thanh toán không khớp", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(27003, "Đơn đặt hàng không tìm thấy", HttpStatus.NOT_FOUND),

    // Others
    FILE_UPLOAD_FAILED(39001, "Tải file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    JSON_PARSE_ERROR(39002, "Lỗi xử lý JSON", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST_BODY(39003, "Thân yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),

    UNCATEGORIZED_EXCEPTION(39999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
