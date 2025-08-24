package com.bmt.java_bmt.implementations;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.services.ICloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FilmImpl implements IFilmService {
    IFilmMapper filmMapper;
    IUserRepository userRepository;
    IFilmRepository filmRepository;
    IFilmProfessionalRepository filmProfessionalRepository;
    ICloudinaryService cloudinaryService;

    private String uploadIfPresent(MultipartFile file, UUID filmId, String type) throws IOException {
        return (file != null && !file.isEmpty())
                ? cloudinaryService.uploadFile(file, filmId.toString(), Others.FILM, type)
                : null;
    }

    @Override
    @Transactional
    public CreateFilmResponse createFilm(CreateFilmRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST));
        Set<FilmProfessional> filmProfessionals = Optional.ofNullable(request.getFilmProfessionalIds())
                .orElse(Set.<UUID>of())
                .stream()
                .map(id -> filmProfessionalRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)))
                .collect(Collectors.toSet());
        Film film = filmMapper.toFilm(request);

        film.setChangedBy(user);

        filmRepository.saveAndFlush(film);

        OtherFilmInformation otherInfo = new OtherFilmInformation();

        otherInfo.setFilm(film);

        try {
            otherInfo.setPosterUrl(uploadIfPresent(request.getImage(), film.getId(), Others.IMAGE));
            otherInfo.setTrailerUrl(uploadIfPresent(request.getVideo(), film.getId(), Others.VIDEO));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        film.setOtherFilmInformation(otherInfo);
        film.setFilmProfessionals(filmProfessionals);

        filmRepository.save(film);

        return filmMapper.toCreateFilmResponse(film);
    }
}
