package com.bmt.java_bmt.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.SeatType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "se_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "se_seat_number", length = 16, nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "se_seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "se_price", nullable = false)
    private Integer price;

    @CreationTimestamp
    @Column(name = "se_created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "se_updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_id", nullable = false)
    private Auditorium auditorium;
}
