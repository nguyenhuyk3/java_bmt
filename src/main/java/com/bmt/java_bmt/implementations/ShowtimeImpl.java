package com.bmt.java_bmt.implementations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.responses.showtime.AddShowtimeResponse;
import com.bmt.java_bmt.entities.Showtime;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.repositories.IAuditoriumRepository;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.repositories.IShowtimeRepository;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IShowtimeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeImpl implements IShowtimeService {
    IFilmRepository filmRepository;
    IShowtimeRepository showtimeRepository;
    IAuditoriumRepository auditoriumRepository;
    IUserRepository userRepository;

    @Transactional
    @Override
    public AddShowtimeResponse addShowtime(AddShowtimeRequest request) {
        var user = userRepository
                .findById(UUID.fromString(
                        SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        var film = filmRepository
                .findById(request.getFilmId())
                .orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));
        var auditorium = auditoriumRepository
                .findById(request.getAuditoriumId())
                .orElseThrow(() -> new AppException(ErrorCode.AUDITORIUM_NOT_FOUND));

        // Kiểm tra showDate có phải hôm nay không
        // Nếu là ngày hôm nay thì không thể tiếp tục
        if (request.getShowDate().isEqual(LocalDate.now())) {
            throw new AppException(ErrorCode.CANNOT_ADD_SHOWTIME_FOR_TODAY);
        }

        LocalDateTime startTime;
        var lastestShowtimeInDay = showtimeRepository.getLastestShowtimeByAuditoriumIdAndByShowDate(
                request.getAuditoriumId(), request.getShowDate());

        if (lastestShowtimeInDay == null) {
            // Nếu chưa có suất chiếu nào → bắt đầu từ 9:00 AM
            startTime = request.getShowDate().atTime(9, 0);
        } else {
            // Nếu suất chiếu cuối cùng không cùng ngày với request.getShowDate() → lỗi
            if (!lastestShowtimeInDay.toLocalDate().isEqual(request.getShowDate())) {
                throw new AppException(ErrorCode.SHOWTIME_OVERFLOW_TO_NEXT_DAY);
            }

            // Nếu đã có suất chiếu trước đó → bắt đầu sau suất chiếu cuối cùng (thêm 20 phút buffer)
            startTime = lastestShowtimeInDay.plusMinutes(20);
        }

        Duration duration = Duration.ofHours(film.getDuration().getHour())
                .plusMinutes(film.getDuration().getMinute());
        var endTime = startTime.plus(duration);
        var savedShowtime = showtimeRepository.save(Showtime.builder()
                .coefficient(request.getCoefficient())
                .showDate(request.getShowDate())
                .startTime(startTime)
                .endTime(endTime)
                .isReleased(false)
                .auditorium(auditorium)
                .film(film)
                .changedBy(user)
                .build());

        return AddShowtimeResponse.builder()
                .id(savedShowtime.getId())
                .coefficient(savedShowtime.getCoefficient())
                .showDate(savedShowtime.getShowDate())
                .startTime(savedShowtime.getStartTime())
                .endTime(savedShowtime.getEndTime())
                .isReleased(savedShowtime.getIsReleased())
                .auditoriumId(savedShowtime.getAuditorium().getId())
                .filmId(savedShowtime.getFilm().getId())
                .build();
    }
}
