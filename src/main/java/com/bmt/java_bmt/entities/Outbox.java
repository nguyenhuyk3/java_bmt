package com.bmt.java_bmt.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "outboxes")
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "os_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "os_event_type", nullable = false, length = 64)
    String eventType;

    @Column(name = "os_payload", nullable = false, columnDefinition = "json")
    String payload;

    @CreationTimestamp
    @Column(name = "os_created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}
