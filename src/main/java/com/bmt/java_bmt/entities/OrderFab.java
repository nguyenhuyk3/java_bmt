package com.bmt.java_bmt.entities;

import java.util.UUID;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_fabs")
public class OrderFab {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "of_id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "of_quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fab_id", nullable = false)
    private FoodAndBeverage foodAndBeverage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", nullable = false)
    private Order order;
}
