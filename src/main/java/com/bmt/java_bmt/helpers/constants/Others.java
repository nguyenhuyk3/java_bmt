package com.bmt.java_bmt.helpers.constants;

public final class Others {
    private Others() {}

    // Role
    public static final String MANAGER = "MANAGER";
    public static final String CUSTOMER = "CUSTOMER";

    // Media type
    public static final String VIDEO = "VIDEO";
    public static final String IMAGE = "IMAGE";

    // Product type
    public static final String FILM = "FILM";
    public static final String FAB = "FAB";

    // Outbox event type
    public static final String FILM_CREATED = "FILM_CREATED";
    public static final String FILM_UPDATED = "FILM_UPDATED";
    public static final String SHOWTIME_RELEASED = "SHOWTIME_RELEASED";
    public static final String PAYMENT_SUCCESS = "PAYMENT_SUCCESS";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";

    // Kafka topic
    public static final String OUTBOX = "outbox_events.bmt_database.outboxes";
}
