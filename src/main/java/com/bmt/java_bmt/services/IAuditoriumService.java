package com.bmt.java_bmt.services;

import java.util.List;
import java.util.UUID;

import com.bmt.java_bmt.dto.responses.auditorium.GetAuditoriumResponse;

public interface IAuditoriumService {
    List<GetAuditoriumResponse> getAllAuditoriumsByCinemaId(UUID cinemaId);
}
