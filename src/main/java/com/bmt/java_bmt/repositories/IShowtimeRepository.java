package com.bmt.java_bmt.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdAndShowDateProjection;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdProjection;
import com.bmt.java_bmt.dto.responses.showtime.ReleasedShowtimeProjection;
import com.bmt.java_bmt.entities.Showtime;

import io.lettuce.core.dynamic.annotation.Param;

public interface IShowtimeRepository extends JpaRepository<Showtime, UUID> {
    @Query(
            value = "SELECT sh_end_time " + "FROM showtimes "
                    + "WHERE a_id = :auditoriumId "
                    + "AND sh_show_date = :showDate "
                    + "ORDER BY sh_created_at DESC "
                    + "LIMIT 1",
            nativeQuery = true)
    LocalDateTime getLastestShowtimeByAuditoriumIdAndByShowDate(
            @Param("auditoriumId") UUID auditoriumId, @Param("showDate") LocalDate showDate);
    /*
    	Một default method có thân hàm (implementation) ngay trong interface.
    	Nó cho phép bạn định nghĩa logic mặc định cho những method có thể dùng lại mà
    không bắt buộc tất cả các class implement interface này phải override.
    */
    //    default LocalDateTime getLatestEndTime(UUID auditoriumId, LocalDate showDate) {
    //        return getLastestShowtimeByAuditoriumIdAndByShowDate(auditoriumId, showDate)
    //                .stream()
    //                .findFirst()
    //                .orElse(null);
    //    }
    @Modifying
    @Transactional
    @Query(
            value =
                    """
			UPDATE showtimes
			SET sh_is_released = NOT sh_is_released,
				sh_updated_at = NOW()
			WHERE sh_id = :id
			""",
            nativeQuery = true)
    int releaseShowtime(@Param("id") UUID id);

    @Query(
            value =
                    """
		SELECT
			BIN_TO_UUID(sh.sh_id) AS showtimeId,
			sh.sh_show_date AS showDate,
			sh.sh_start_time AS startTime,
			sh.sh_end_time AS endTime,
			sh.sh_coefficient AS coefficient,

			BIN_TO_UUID(f.f_id) AS filmId,
			f.f_title AS filmTitle,
			f.f_description AS filmDescription,
			f.f_release_date AS filmReleaseDate,
			f.f_duration AS filmDuration,
			ofi.ofi_poster_url AS posterUrl,
			ofi.ofi_trailer_url AS trailerUrl,

			GROUP_CONCAT(DISTINCT fg.fg_genre SEPARATOR ',') AS genres,

			JSON_ARRAYAGG(
				JSON_OBJECT(
					'id', BIN_TO_UUID(fpf.fpf_id),
					'job', fpf.fpf_job,
					'name', pi.pi_full_name,
					'avatarUrl', pi.pi_avatar_url
				)
			) AS filmProfessionals

		FROM showtimes sh
		JOIN films f ON sh.f_id = f.f_id
		LEFT JOIN other_film_informations ofi ON f.f_id = ofi.f_id
		LEFT JOIN film_genres fg ON f.f_id = fg.f_id
		LEFT JOIN film_people fpp ON f.f_id = fpp.f_id
		LEFT JOIN film_professionals fpf ON fpp.fpf_id = fpf.fpf_id
		LEFT JOIN personal_information pi ON fpf.pi_id = pi.pi_id

		WHERE sh.a_id = :auditoriumId
		AND sh.sh_show_date = :showDate
		AND sh.sh_is_released = false

		GROUP BY
			sh.sh_id, sh.sh_show_date, sh.sh_start_time, sh.sh_end_time, sh.sh_coefficient,
			f.f_id, f.f_title, f.f_description, f.f_release_date, f.f_duration,
			ofi.ofi_poster_url, ofi.ofi_trailer_url
		""",
            nativeQuery = true)
    List<GetShowtimeByAuditoriumIdAndShowDateProjection> findShowtimesByAuditoriumIdAndShowDate(
            @Param("auditoriumId") UUID auditoriumId, @Param("showDate") LocalDate showDate);

    @Query(
            value =
                    """
		SELECT
			BIN_TO_UUID(sh.sh_id) AS showtimeId,
			sh.sh_show_date AS showDate,
			sh.sh_start_time AS startTime,
			sh.sh_end_time AS endTime
		FROM showtimes sh
		WHERE sh.sh_is_released = true
		AND sh.sh_show_date >= CURRENT_DATE
		ORDER BY sh.sh_show_date ASC, sh.sh_start_time ASC
		""",
            nativeQuery = true)
    List<ReleasedShowtimeProjection> findAllReleasedShowtimesFromToday();

    @Query(
            value =
                    """
		SELECT
			BIN_TO_UUID(sh.sh_id) AS showtimeId,
			sh.sh_show_date AS showDate,
			sh.sh_start_time AS startTime,
			sh.sh_end_time AS endTime,
			sh.sh_coefficient AS coefficient
		FROM showtimes sh
		WHERE sh.a_id = :auditoriumId AND sh.sh_is_released = true
		ORDER BY sh.sh_show_date ASC, sh.sh_start_time ASC
		""",
            nativeQuery = true)
    List<GetShowtimeByAuditoriumIdProjection> findAllReleasedShowtimesByAuditoriumId(@Param("auditoriumId") UUID auditoriumId);
}
