package com.bmt.java_bmt.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.auditorium.GetAuditoriumResponse;
import com.bmt.java_bmt.repositories.IAuditoriumRepository;
import com.bmt.java_bmt.services.IAuditoriumService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@AllArgsConstructor
public class AuditoriumImpl implements IAuditoriumService {
    IAuditoriumRepository auditoriumRepository;

    @Override
    public List<GetAuditoriumResponse> getAllAuditoriumsByCinemaId(UUID cinemaId) {
        return auditoriumRepository.findByCinemaId(cinemaId).stream()
                .map(auditorium -> GetAuditoriumResponse.builder()
                        .id(auditorium.getId())
                        .name(auditorium.getName())
                        .numberOfSeats(auditorium.getSeatCapacity())
                        .build())
                .toList();
    }
}
