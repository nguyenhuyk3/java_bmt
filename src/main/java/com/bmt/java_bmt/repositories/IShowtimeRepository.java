package com.bmt.java_bmt.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
