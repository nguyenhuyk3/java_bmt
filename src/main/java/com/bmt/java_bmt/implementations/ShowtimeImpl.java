package com.bmt.java_bmt.implementations;

import static java.util.Arrays.stream;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.requests.showtime.ReleaseShowtimeRequest;
import com.bmt.java_bmt.dto.responses.film.GetFilmResponse;
import com.bmt.java_bmt.dto.responses.filmProfessional.GetFilmProfessionalResponse;
import com.bmt.java_bmt.dto.responses.showtime.*;
import com.bmt.java_bmt.entities.Outbox;
import com.bmt.java_bmt.entities.Showtime;
import com.bmt.java_bmt.entities.enums.Genre;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.repositories.*;
import com.bmt.java_bmt.services.IShowtimeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeImpl implements IShowtimeService {
    IFilmRepository filmRepository;
    IShowtimeRepository showtimeRepository;
    IAuditoriumRepository auditoriumRepository;
    IUserRepository userRepository;
    IShowtimeSeatRepository showtimeSeatRepository;
    IOutboxRepository outboxRepository;
    ObjectMapper objectMapper;

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

    @Transactional
    @Override
    public String releaseShowtime(ReleaseShowtimeRequest request) {
        if (!showtimeRepository.existsById(request.getId())) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_FOUND);
        }

        if (showtimeRepository.releaseShowtime(request.getId()) == 0) {
            throw new AppException(ErrorCode.RELEASE_SHOWTIME_FAILED);
        }

        try {
            Id showtimeId = Id.builder().id(request.getId().toString()).build();

            outboxRepository.save(Outbox.builder()
                    .eventType(Others.SHOWTIME_RELEASED)
                    .payload(objectMapper.writeValueAsString(showtimeId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }

        //        if (showtimeSeatRepository.createShowtimeSeats(request.getId()) != NUMBER_OF_SEATS) {
        //            throw new AppException(ErrorCode.NOT_ENOUGH_SHOWTIME_SEATS);
        //        }

        return "Công bố xuất chiếu thành công";
    }

    @Override
    public LocalDateTime getLastestShowtimeByAuditoriumIdAndByShowDate(UUID auditoriumId, LocalDate showDate) {
        return showtimeRepository.getLastestShowtimeByAuditoriumIdAndByShowDate(auditoriumId, showDate);
    }

    private GetShowtimeByAuditoriumIdAndShowDateResponse mapToGetShowtimeByAuditoriumIdAndShowDateResponse(
            GetShowtimeByAuditoriumIdAndShowDateProjection p) {
        // parse genres
        Set<Genre> genres = stream(p.getGenres().split(",")).map(Genre::valueOf).collect(Collectors.toSet());
        // parse film professionals JSON
        Set<GetFilmProfessionalResponse> professionals = new HashSet<>();

        if (p.getFilmProfessionals() != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                professionals = Set.copyOf(
                        Arrays.asList(mapper.readValue(p.getFilmProfessionals(), GetFilmProfessionalResponse[].class)));
            } catch (Exception e) {
                throw new AppException(ErrorCode.FILM_PROFESSIONAL_PARSE_FAILED);
            }
        }

        GetFilmResponse film = GetFilmResponse.builder()
                .id(UUID.fromString(p.getFilmId()))
                .title(p.getFilmTitle())
                .description(p.getFilmDescription())
                .releaseDate(p.getFilmReleaseDate())
                .duration(p.getFilmDuration())
                .posterUrl(p.getPosterUrl())
                .trailerUrl(p.getTrailerUrl())
                .genres(genres)
                .filmProfessionals(professionals)
                .build();

        return GetShowtimeByAuditoriumIdAndShowDateResponse.builder()
                .showtimeId(UUID.fromString(p.getShowtimeId()))
                .showDate(p.getShowDate())
                .startTime(p.getStartTime())
                .endTime(p.getEndTime())
                .coefficient(p.getCoefficient())
                .film(film)
                .build();
    }

    @Override
    public List<GetShowtimeByAuditoriumIdAndShowDateResponse> findShowtimesByAuditoriumIdAndShowDate(
            UUID auditoriumId, LocalDate showDate) {
        if (!auditoriumRepository.existsById(auditoriumId)) {
            throw new AppException(ErrorCode.AUDITORIUM_NOT_FOUND);
        }

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (showDate.isBefore(tomorrow)) {
            throw new AppException(ErrorCode.SHOWDATE_MUST_START_FROM_TOMORROW);
        }

        var rawResult = showtimeRepository.findShowtimesByAuditoriumIdAndShowDate(auditoriumId, showDate);

        if (rawResult.isEmpty()) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_FOUND_FOR_DATE);
        }

        return rawResult.stream()
                .map(this::mapToGetShowtimeByAuditoriumIdAndShowDateResponse)
                .toList();
    }

    @Override
    public List<ReleasedShowtimeProjection> findAllReleasedShowtimesFromToday() {
        return showtimeRepository.findAllReleasedShowtimesFromToday();
    }

    @Override
    public List<GetShowtimeByAuditoriumIdProjection> findAllReleasedShowtimesByAuditoriumId(UUID auditoriumId) {
        return showtimeRepository.findAllReleasedShowtimesByAuditoriumId(auditoriumId);
    }
}
