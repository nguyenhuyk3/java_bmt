package com.bmt.java_bmt.dto.responses.authentication.forgotPassword;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteForgotPasswordResponse {
    String email;
    String password;
}
