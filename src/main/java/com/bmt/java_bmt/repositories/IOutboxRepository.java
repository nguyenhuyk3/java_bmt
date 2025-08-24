package com.bmt.java_bmt.repositories;

import com.bmt.java_bmt.entities.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOutboxRepository extends JpaRepository<Outbox, UUID> {
}
