package com.bmt.java_bmt.entities;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Getter
@Setter
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
    @Column(name = "a_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "a_name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(name = "a_seat_capacity", nullable = false)
    private Integer seatCapacity;

    @Column(name = "a_is_released", nullable = false)
    private Boolean isReleased;

    @CreationTimestamp
    /*
        Annotation của Hibernate.
        Khi một entity mới được insert vào database, Hibernate sẽ tự động set thời gian hiện tại cho trường này.
        Nó chỉ được gán lúc tạo mới (insert), không update lại khi entity thay đổi.
     */
    @Column(name = "a_created_at")
    private Instant createdAt;

    @UpdateTimestamp
    /*
        Annotation của Hibernate.
        Nó tự động set giá trị thời gian hiện tại mỗi khi entity được update (merge/flush).
        Khác với @CreationTimestamp chỉ set lúc insert,
     thì @UpdateTimestamp sẽ cập nhật lại mỗi lần entity thay đổi và được lưu lại.
     */
    @Column(name = "a_updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id", nullable = false)
    /*
        @JoinColumn là gì?
            - Nó dùng để chỉ định cột khóa ngoại (foreign key) trong bảng hiện tại,
            giúp JPA/Hibernate biết cách liên kết hai entity với nhau.
     */
    private Cinema cinema;

    @OneToMany(mappedBy = "auditorium")
    private Set<Seat> seats;

    @OneToMany(mappedBy = "auditorium")
    private Set<Showtime> showtimes;
}
