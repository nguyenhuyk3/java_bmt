package com.bmt.java_bmt.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "showtimes")
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sh_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sh_coefficient", nullable = false)
    private Integer coefficient;

    @Column(name = "sh_show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "sh_start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "sh_end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "sh_is_released", nullable = false)
    private Boolean isReleased;

    @CreationTimestamp
    @Column(name = "sh_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "sh_updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "a_id", nullable = false)
    private Auditorium auditorium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_id", nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_changed_by", nullable = false)
    private User changedBy;

    @OneToMany(mappedBy = "showtime")
    private Set<ShowtimeSeat> showtimeSeats;
}
