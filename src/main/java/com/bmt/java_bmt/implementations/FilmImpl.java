package com.bmt.java_bmt.implementations;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.requests.film.UpdateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.entities.*;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.mappers.IFilmMapper;
import com.bmt.java_bmt.repositories.IFilmProfessionalRepository;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.repositories.IOutboxRepository;
import com.bmt.java_bmt.repositories.IUserRepository;
import com.bmt.java_bmt.services.ICloudinaryService;
import com.bmt.java_bmt.services.IFilmService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    ICloudinaryService cloudinaryService;
    IOutboxRepository outboxRepository;
    ObjectMapper objectMapper;

    private String uploadIfPresent(MultipartFile file, UUID filmId, String type, boolean isDeleted, String url)
            throws IOException {
        if (isDeleted) {
            cloudinaryService.deleteFile(url, type);
        }

        return (file != null && !file.isEmpty())
                ? cloudinaryService.uploadFile(file, filmId.toString(), Others.FILM, type)
                : null;
    }

    @Override
    @Transactional
    public CreateFilmResponse createFilm(CreateFilmRequest request) {
        User user = userRepository
                .findById(UUID.fromString(
                        SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        Set<FilmProfessional> filmProfessionals =
                Optional.ofNullable(request.getFilmProfessionalIds()).orElse(Set.<UUID>of()).stream()
                        .map(id -> filmProfessionalRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)))
                        .collect(Collectors.toSet());
        Film film = filmMapper.toFilm(request);

        film.setChangedBy(user);

        filmRepository.saveAndFlush(film);

        OtherFilmInformation otherInfo = new OtherFilmInformation();

        otherInfo.setFilm(film);

        try {
            otherInfo.setTrailerUrl(uploadIfPresent(request.getVideo(), film.getId(), Others.VIDEO, false, ""));
            otherInfo.setPosterUrl(uploadIfPresent(request.getImage(), film.getId(), Others.IMAGE, false, ""));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        film.setOtherFilmInformation(otherInfo);
        film.setFilmProfessionals(filmProfessionals);

        var savedFilm = filmRepository.save(film);

        try {
            Id filmId = Id.builder().id(savedFilm.getId().toString()).build();

            outboxRepository.save(Outbox.builder()
                    .eventType(Others.FILM_CREATED)
                    .payload(objectMapper.writeValueAsString(filmId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }

        return filmMapper.toCreateFilmResponse(film);
    }

    @Override
    public CreateFilmResponse updateFilm(UpdateFilmRequest request) {
        Film film =
                filmRepository.findById(request.getId()).orElseThrow(() -> new AppException(ErrorCode.FILM_NOT_FOUND));

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            film.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            film.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            film.setDuration(request.getDuration());
        }
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            film.setGenres(request.getGenres());
        }
        if (request.getFilmProfessionalIds() != null
                && !request.getFilmProfessionalIds().isEmpty()) {
            Set<FilmProfessional> filmProfessionals =
                    Optional.ofNullable(request.getFilmProfessionalIds()).orElse(Set.<UUID>of()).stream()
                            .map(id -> filmProfessionalRepository
                                    .findById(id)
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)))
                            .collect(Collectors.toSet());

            film.setFilmProfessionals(filmProfessionals);
        }

        OtherFilmInformation otherInfo = film.getOtherFilmInformation();

        try {
            if (request.getVideo() != null) {
                otherInfo.setTrailerUrl(uploadIfPresent(
                        request.getVideo(),
                        film.getId(),
                        Others.VIDEO,
                        true,
                        film.getOtherFilmInformation().getTrailerUrl()));
            }
            if (request.getImage() != null) {
                otherInfo.setPosterUrl(uploadIfPresent(
                        request.getImage(),
                        film.getId(),
                        Others.IMAGE,
                        true,
                        film.getOtherFilmInformation().getPosterUrl()));
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        otherInfo.setFilm(film);

        filmRepository.save(film);

        try {
            Id filmId = Id.builder().id(request.getId().toString()).build();

            outboxRepository.save(Outbox.builder()
                    .eventType(Others.FILM_UPDATED)
                    .payload(objectMapper.writeValueAsString(filmId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }

        return filmMapper.toCreateFilmResponse(film);
    }
}
