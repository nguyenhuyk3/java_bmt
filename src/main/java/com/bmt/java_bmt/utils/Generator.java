package com.bmt.java_bmt.utils;

import java.security.SecureRandom;

public class Generator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP(int digits) {
        int max = (int) Math.pow(10, digits) - 1; // ví dụ digits=6 -> max=999999
        int min = (int) Math.pow(10, digits - 1); // ví dụ digits=6 -> min=100000
        int otp = random.nextInt(max - min + 1) + min;

        return String.format("%0" + digits + "d", otp);
    }
}
