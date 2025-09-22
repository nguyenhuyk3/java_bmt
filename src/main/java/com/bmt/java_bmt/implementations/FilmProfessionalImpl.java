package com.bmt.java_bmt.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.responses.filmProfessional.GetFilmProfessionalResponse;
import com.bmt.java_bmt.repositories.IFilmProfessionalRepository;
import com.bmt.java_bmt.services.IFilmProfessionalService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@AllArgsConstructor
public class FilmProfessionalImpl implements IFilmProfessionalService {
    IFilmProfessionalRepository filmProfessionalRepository;

    @Override
    public List<GetFilmProfessionalResponse> getAll() {
        return filmProfessionalRepository.findAll().stream()
                .map(filmProfessional -> GetFilmProfessionalResponse.builder()
                        .id(filmProfessional.getId())
                        .job(filmProfessional.getJob().name())
                        .name(filmProfessional.getPersonalInformation().getFullName())
                        .avatarUrl(filmProfessional.getPersonalInformation().getAvatarUrl())
                        .build())
                .toList();
    }
}
