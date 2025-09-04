package com.bmt.java_bmt.helpers.constants;

public final class RedisKey {
    private RedisKey() {}

    // Registration
    public static final String IS_IN_REGISTRATION_PROCESS = "is_in_registration_process::";
    public static final String REGISTRATION_OTP = "registration_otp::";
    public static final String REGISTRATION_COMPLETION = "registration_completion::";

    // Forgot password
    public static final String IS_IN_FORGOT_PASSWORD_PROCESS = "is_in_forgot_password_process::";
    public static final String FORGOT_PASSWORD_OTP = "forgot_password_otp::";
    public static final String FORGOT_PASSWORD_COMPLETION = "forgot_password_completion::";

    // Showtime seat
    public static final String SHOWTIME_SEATS = "showtime_seats::";

    // Others
    public static final String TOTAL_OF_ORDER = "total_of_order::";
    public static final String FAVORITE_FILMS = "FAVORITE_FILMS::";
}
