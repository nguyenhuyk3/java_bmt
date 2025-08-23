package com.bmt.java_bmt.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
/*
	@Builder là một annotation của Lombok giúp bạn
tự động tạo "Builder pattern" cho class Java — cho phép bạn khởi tạo đối tượng một cách linh hoạt,
dễ đọc và không cần constructor dài dòng.
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APIResponse<T> {
    T result;

    @Builder.Default
    /*
    	@Builder.Default là một annotation của thư viện Lombok trong Java.
    	Nó được sử dụng khi bạn dùng @Builder để tạo pattern Builder cho class,
    và muốn gán giá trị mặc định cho một field nếu nó không được set khi build object.
    */
    int code = 1000;

    String message;
    Map<String, String> error;
    Map<String, String> errors;
}
