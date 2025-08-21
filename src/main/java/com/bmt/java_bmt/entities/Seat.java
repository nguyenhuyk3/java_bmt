package com.bmt.java_bmt.entities;

import com.bmt.java_bmt.entities.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "se_id", length = 36, nullable = false)
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
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "se_updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_id", nullable = false)
    private Auditorium auditorium;
}