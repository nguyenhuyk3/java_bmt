package com.bmt.java_bmt.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.FabType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "foods_and_beverages")
public class FoodAndBeverage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fab_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fab_name", columnDefinition = "TEXT", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "fab_type", nullable = false)
    private FabType type;

    @Column(name = "fab_image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "fab_price", nullable = false)
    private Integer price;

    @Column(name = "fab_is_deleted", nullable = false)
    private Boolean isDeleted;

    @CreationTimestamp
    @Column(name = "fab_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "fab_updated_at")
    private LocalDateTime updatedAt;
}
