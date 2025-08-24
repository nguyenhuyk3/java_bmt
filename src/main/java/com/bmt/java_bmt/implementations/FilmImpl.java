package com.bmt.java_bmt.implementations;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.entities.Film;
import com.bmt.java_bmt.entities.FilmProfessional;
import com.bmt.java_bmt.entities.OtherFilmInformation;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.mappers.IFilmMapper;
import com.bmt.java_bmt.repositories.IFilmProfessionalRepository;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.IFilmService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FilmImpl implements IFilmService {
    IFilmMapper filmMapper;
    IUserRepository userRepository;
    IFilmRepository filmRepository;
    IFilmProfessionalRepository filmProfessionalRepository;

    @Override
    @Transactional
    public CreateFilmResponse createFilm(CreateFilmRequest request) {
        User user = userRepository
                .findById(UUID.fromString(
                        SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        Set<FilmProfessional> filmProfessionals = request.getFilmProfessionalIds() != null
                ? request.getFilmProfessionalIds().stream()
                .map(id -> filmProfessionalRepository
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)))
                .collect(Collectors.toSet())
                : Set.of();
        Film film = filmMapper.toFilm(request);
        OtherFilmInformation otherFilmInformation =
                filmMapper.toOtherFilmInformation(request.getOtherFilmInformation());

        otherFilmInformation.setFilm(film);

        film.setChangedBy(user);
        film.setOtherFilmInformation(otherFilmInformation);
        film.setFilmProfessionals(filmProfessionals);

        filmRepository.save(film);

        return filmMapper.toCreateFilmResponse(film);
    }
}
