package com.bmt.java_bmt.helpers.constants;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public final class RedisKey {
    // Registration
    static String IS_IN_REGISTRATION_PROCESS = "is_in_registration_process::";
    static String REGISTRATION_OTP = "registration_otp::";
    static String REGISTRATION_COMPLETION = "registration_completion::";

    // Forgot password
    static String IS_IN_FORGOT_PASSWORD_PROCESS = "is_in_forgot_password_process::";
    static String FORGOT_PASSWORD_OTP = "forgot_password_otp::";
    static String FORGOT_PASSWORD_COMPLETION = "forgot_password_completion::";
}
