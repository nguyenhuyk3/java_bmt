package com.bmt.java_bmt.implementations;

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
        if (!showtimeRepository.existsById(id)) {
            throw new AppException((ErrorCode.SHOWTIME_NOT_FOUND));
        }

        return showtimeSeatRepository.getShowtimeSeatsByShowtimeId(id);
    }
}
