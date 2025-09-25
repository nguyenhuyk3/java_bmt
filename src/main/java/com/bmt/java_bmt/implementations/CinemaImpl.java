package com.bmt.java_bmt.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.cinema.GetCinemaResponse;
import com.bmt.java_bmt.entities.enums.City;
import com.bmt.java_bmt.repositories.ICinemaRepository;
import com.bmt.java_bmt.services.ICinemaService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@AllArgsConstructor
public class CinemaImpl implements ICinemaService {
    ICinemaRepository cinemaRepository;

    @Override
    public List<GetCinemaResponse> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(cinema -> GetCinemaResponse.builder()
                        .id(cinema.getId())
                        .name(cinema.getName())
                        .location(cinema.getLocation())
                        .city(City.toVietnamese(cinema.getCity()))
                        .numberOfRooms((int) cinema.getAuditoriums().stream().count())
                        .build())
                .toList();
    }
}
