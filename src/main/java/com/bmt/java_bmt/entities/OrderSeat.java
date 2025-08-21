package com.bmt.java_bmt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_seats")
public class OrderSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "os_id", length = 36, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private Seat seat;
}
