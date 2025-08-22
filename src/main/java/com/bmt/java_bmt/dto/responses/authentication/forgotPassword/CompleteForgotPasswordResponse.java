package com.bmt.java_bmt.dto.responses.authentication.forgotPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompleteForgotPasswordResponse {
    String email;
    String password;
}
