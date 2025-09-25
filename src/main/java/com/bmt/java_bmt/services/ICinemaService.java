package com.bmt.java_bmt.services;

import java.util.List;

import com.bmt.java_bmt.dto.responses.cinema.GetCinemaResponse;

public interface ICinemaService {
    List<GetCinemaResponse> getAllCinemas();
}
