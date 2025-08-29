package com.bmt.java_bmt.repositories;

import com.bmt.java_bmt.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOrderRepository extends JpaRepository<Order, UUID> {
}
