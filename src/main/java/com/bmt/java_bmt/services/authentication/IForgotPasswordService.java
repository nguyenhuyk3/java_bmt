package com.bmt.java_bmt.services.authentication;

import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.CompleteForgotPasswordRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.SendForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.forgotPassword.VerifyForgotPasswordOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.forgotPassword.CompleteForgotPasswordResponse;

public interface IForgotPasswordService {
    String sendForgotPasswordOTP(SendForgotPasswordOTPRequest request);
    String verifyForgotPasswordOTP(VerifyForgotPasswordOTPRequest request);
    CompleteForgotPasswordResponse completeFortgotPassword(CompleteForgotPasswordRequest request);
}
