package com.bmt.java_bmt.repositories;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
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
    @Query(
            value =
                    """
			INSERT INTO showtime_seats ( sh_id, se_id, ss_status, ss_created_at)
			SELECT
				:showtimeId AS sh_id,
				s.se_id AS se_id,
				'AVAILABLE' AS ss_status,
				NOW() AS ss_created_at
			FROM showtimes sh
			JOIN seats s ON s.a_id = sh.a_id
			WHERE sh.sh_id = :showtimeId
			""",
            nativeQuery = true)
    int createShowtimeSeats(@Param("showtimeId") UUID showtimeId);

    @Query(
            value =
                    """
			SELECT
				BIN_TO_UUID(ss.se_id) AS seatId,
				s.se_seat_type AS seatType,
				s.se_seat_number AS seatNumber,
				s.se_price AS price,
				ss.ss_status AS status,
				BIN_TO_UUID(ss.u_booked_by) AS bookedBy,
				ss.ss_created_at AS createdAt
			FROM showtime_seats ss
			INNER JOIN seats s ON s.se_id = ss.se_id
			WHERE ss.sh_id = :showtimeId
			ORDER BY LENGTH(s.se_seat_number), s.se_seat_number
			""",
            nativeQuery = true)
    List<GetShowtimeSeatResponse> getShowtimeSeatsByShowtimeId(@Param("showtimeId") UUID showtimeId);

    @Modifying
    @Transactional
    @Query(
            value =
                    """
			UPDATE showtime_seats
			SET ss_status = :status
			WHERE sh_id = :showtimeId AND u_booked_by = :userId
			""",
            nativeQuery = true)
    int updateStatusOfSeatsByUserIdAndShowtimeId(
            @Param("status") String status, @Param("userId") UUID userId, @Param("showtimeId") UUID showtimeId);

    @Modifying
    @Transactional
    @Query(
            value =
                    """
			UPDATE showtime_seats
			SET ss_status = :status
			WHERE se_id = :seatId AND sh_id = :showtimeId
			""",
            nativeQuery = true)
    int updateStatusOfSeatsWhenNotPaidBySeatIdAndShowtimeId(
            @Param("status") String status, @Param("seatId") UUID seatId, @Param("showtimeId") UUID showtimeId);

    @Modifying
    @Transactional
    @Query(
            value =
                    """
			UPDATE showtime_seats
			SET
				ss_status = :status,
				u_booked_by = :userId,
				ss_booked_at = NOW()
			WHERE sh_id = :showtimeId AND se_id = :seatId
			""",
            nativeQuery = true)
    int updateSeatsBySeatIdAndShowtimeId(
            @Param("status") String status,
            @Param("userId") UUID userId,
            @Param("seatId") UUID seatId,
            @Param("showtimeId") UUID showtimeId);
}
