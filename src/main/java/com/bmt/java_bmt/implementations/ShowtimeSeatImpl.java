package com.bmt.java_bmt.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.repositories.IShowtimeRepository;
import com.bmt.java_bmt.repositories.IShowtimeSeatRepository;
import com.bmt.java_bmt.services.IShowtimeSeatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeSeatImpl implements IShowtimeSeatService {
    IShowtimeSeatRepository showtimeSeatRepository;
    IShowtimeRepository showtimeRepository;

    @Override
    public List<GetShowtimeSeatResponse> getShowtimeSeatsByShowtimeId(UUID id) {
        var showtime =
                showtimeRepository.findById(id).orElseThrow(() -> new AppException((ErrorCode.SHOWTIME_NOT_FOUND)));

        if (!showtime.getIsReleased()) {
            throw new AppException(ErrorCode.SHOWTIME_IS_NOT_RELEASED);
        }

        if (showtime.getShowDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.SHOWTIME_IS_IN_PAST);
        }

        return showtimeSeatRepository.getShowtimeSeatsByShowtimeId(id);
    }
}
