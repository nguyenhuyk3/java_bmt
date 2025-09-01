package com.bmt.java_bmt.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Formatter {
    public static String formatDuration(LocalTime time) {
        if (time == null) {
            return "";
        }

        int hours = time.getHour();
        int minutes = time.getMinute();
        int totalMinutes = time.toSecondOfDay() / 60;

        // Nếu chỉ muốn hiển thị tổng số phút (ví dụ: "181 phút")
        if (hours == 0) {
            return totalMinutes + " phút";
        }

        // Nếu muốn hiển thị cả giờ và phút (ví dụ: "2h 59 phút")
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(" giờ");
        }

        if (minutes > 0) {
            if (hours > 0) sb.append(" ");
            sb.append(minutes).append(" phút");
        }

        return sb.toString();
    }

    public static String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return date.format(formatter);
    }

    public static String formatReadableTime(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();

        return String.format("%d giờ %d phút", hour, minute);
    }
}
