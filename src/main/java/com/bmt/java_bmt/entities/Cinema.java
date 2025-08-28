package com.bmt.java_bmt.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.City;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cinemas")
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "c_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "c_name", columnDefinition = "TEXT", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_city", nullable = false)
    private City city;

    @Column(name = "c_location", columnDefinition = "TEXT", nullable = false)
    private String location;

    @Column(name = "c_is_released", nullable = false)
    private Boolean isReleased;

    @CreationTimestamp
    @Column(name = "c_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "c_updated_at")
    private LocalDateTime updatedAt;

    /*
    - mappedBy = "cinema" → quan hệ này được ánh xạ (mapping)
    từ phía Auditorium, chứ không tạo cột khóa ngoại ở bảng Cinema.
    - Tức là:
    	+ Trong bảng auditoriums sẽ có cột c_id (FK) trỏ đến cinemas.c_id.
    */
    @OneToMany(mappedBy = "cinema")
    private Set<Auditorium> auditoriums;
}
