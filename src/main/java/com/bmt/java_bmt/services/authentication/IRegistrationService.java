package com.bmt.java_bmt.services.authentication;

import com.bmt.java_bmt.dto.requests.authentication.registration.CompleteRegistrationRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.SendRegistrationOTPRequest;
import com.bmt.java_bmt.dto.requests.authentication.registration.VerifyRegistrationOTPRequest;
import com.bmt.java_bmt.dto.responses.authentication.registration.RegistrationResponse;

public interface IRegistrationService {
    String sendOTP(SendRegistrationOTPRequest request);
    String verifyOTP(VerifyRegistrationOTPRequest request);
    RegistrationResponse completeRegistration(CompleteRegistrationRequest request);
}
