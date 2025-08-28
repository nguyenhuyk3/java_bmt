package com.bmt.java_bmt.entities;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_seats")
public class OrderSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "os_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private Seat seat;
}
