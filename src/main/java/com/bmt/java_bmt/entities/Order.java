package com.bmt.java_bmt.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.bmt.java_bmt.entities.enums.OrderStatus;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "o_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_status", nullable = false)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "o_created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "o_updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sh_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_ordered_by", nullable = false)
    private User orderedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderSeat> orderSeats;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderFab> orderFabs;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}
