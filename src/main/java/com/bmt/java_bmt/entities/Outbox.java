package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outboxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
