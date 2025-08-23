package com.bmt.java_bmt.repositories;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bmt.java_bmt.entities.User;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    /*
    @Modifying → cho JPA biết đây là câu lệnh UPDATE/DELETE, không phải SELECT.
    @Transactional → bắt buộc, vì UPDATE cần transaction.
    Trả về int = số bản ghi được update.
    */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);
}
