package com.bmt.java_bmt.repositories;

import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bmt.java_bmt.entities.ShowtimeSeat;

import io.lettuce.core.dynamic.annotation.Param;

public interface IShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, UUID> {
    /*
    Khi bạn viết một repository method có native INSERT hoặc UPDATE query:
    	- JPA/Hibernate mặc định chỉ cho phép SELECT.
    	- Nếu muốn chạy INSERT/UPDATE/DELETE thì bạn phải đánh dấu
    method đó là câu lệnh thay đổi dữ liệu bằng @Modifying.
    	- Đồng thời, bạn cần 1 transaction để thực thi.
    */
    @Transactional
    @Modifying
    @Query(value = """
    INSERT INTO showtime_seats (ss_id, sh_id, se_id, ss_status, ss_created_at)
    SELECT 
        UUID_TO_BIN(UUID()) AS ss_id,
        :showtimeId AS sh_id,
        s.se_id AS se_id,
        'AVAILABLE' AS ss_status,
        NOW() AS ss_created_at
    FROM showtimes sh
    JOIN seats s ON s.a_id = sh.a_id
    WHERE sh.sh_id = :showtimeId
    """, nativeQuery = true)
    int createShowtimeSeats(@Param("showtimeId") UUID showtimeId);

}
