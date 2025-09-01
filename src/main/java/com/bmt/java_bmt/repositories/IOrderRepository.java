package com.bmt.java_bmt.repositories;

import java.util.Optional;
import java.util.UUID;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.Order;
import org.springframework.data.jpa.repository.Query;

public interface IOrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.showtime s " +
            "JOIN FETCH s.film f " +
            "JOIN FETCH s.auditorium a " +
            "JOIN FETCH a.cinema c " +
            "JOIN FETCH o.orderedBy u " +
            "LEFT JOIN FETCH o.orderSeats os " +
            "LEFT JOIN FETCH os.seat " +
            "LEFT JOIN FETCH o.orderFabs of " +
            "LEFT JOIN FETCH of.foodAndBeverage " +
            "WHERE o.id = :orderId")
    Optional<Order> findOrderDetailsById(@Param("orderId") UUID orderId);
}
