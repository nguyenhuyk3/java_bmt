package com.bmt.java_bmt.entities;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.bmt.java_bmt.entities.enums.SeatStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "showtime_seats")
public class ShowtimeSeat implements Serializable {
    @EmbeddedId
    private ShowtimeSeatId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ss_status", nullable = false)
    private SeatStatus status;

    @Column(name = "ss_booked_at")
    private LocalDateTime bookedAt;

    @CreationTimestamp
    @Column(name = "ss_created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_booked_by", nullable = true)
    private User bookedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    // Dùng @MapsId để liên kết trường 'showtimeId' trong ShowtimeSeatId
    @MapsId("showtimeId")
    @JoinColumn(name = "sh_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    // Tương tự, dùng @MapsId để liên kết trường 'seatId' trong ShowtimeSeatId
    @MapsId("seatId")
    @JoinColumn(name = "se_id", nullable = false)
    private Seat seat;
}
