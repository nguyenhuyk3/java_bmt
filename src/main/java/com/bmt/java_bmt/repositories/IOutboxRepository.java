package com.bmt.java_bmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.java_bmt.entities.Outbox;

public interface IOutboxRepository extends JpaRepository<Outbox, UUID> {}
