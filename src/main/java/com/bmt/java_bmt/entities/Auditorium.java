package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auditoriums")
public class Auditorium {
    /*
            - @GeneratedValue(strategy = GenerationType.UUID) trong JPA/Hibernate
        được dùng để tự động sinh giá trị cho khóa chính (@Id) theo cơ chế UUID
    */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "a_id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "a_name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(name = "a_seat_capacity", nullable = false)
    private Integer seatCapacity;

    @Column(name = "a_is_released", nullable = false)
    private Boolean isReleased;

    @CreationTimestamp
    @Column(name = "a_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "a_updated_at")
    private LocalDateTime updatedAt;

    /*
     - LAZY
         + Khi load entity cha (Auditorium), JPA không load Seat ngay lập tức.
         + Chỉ khi bạn truy cập getter (Auditorium thì Hibernate mới truy vấn DB để lấy dữ liệu Seat
         (kỹ thuật này gọi là lazy loading hoặc proxy).
         + Dễ bị lỗi LazyInitializationException sau khi session/transaction đã đóng.
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    private Cinema cinema;

    @OneToMany(mappedBy = "auditorium")
    private Set<Seat> seats;

    @OneToMany(mappedBy = "auditorium")
    private Set<Showtime> showtimes;
}
