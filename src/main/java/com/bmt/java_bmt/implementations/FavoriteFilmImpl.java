package com.bmt.java_bmt.implementations;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.others.IFilmElasticsearchProjection;
import com.bmt.java_bmt.dto.others.SimplePersonInformation;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.RedisKey;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.services.IFavoriteFilmService;
import com.bmt.java_bmt.services.IRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FavoriteFilmImpl implements IFavoriteFilmService {
    IRedisService redisService;
    IFilmRepository filmRepository;

    ObjectMapper objectMapper;

    long SEVEN_DAYS = 60 * 24 * 7;

    private FilmDocument toFilmDocument(IFilmElasticsearchProjection projection) throws JsonProcessingException {
        List<String> genres =
                (projection.getGenres() != null && !projection.getGenres().isBlank())
                        ? objectMapper.readValue(projection.getGenres(), new TypeReference<List<String>>() {})
                        : Collections.emptyList();
        List<SimplePersonInformation> actors = (projection.getActors() != null
                        && !projection.getActors().isBlank())
                ? objectMapper.readValue(projection.getActors(), new TypeReference<List<SimplePersonInformation>>() {})
                : Collections.emptyList();
        List<SimplePersonInformation> directors =
                (projection.getDirectors() != null && !projection.getDirectors().isBlank())
                        ? objectMapper.readValue(
                                projection.getDirectors(), new TypeReference<List<SimplePersonInformation>>() {})
                        : Collections.emptyList();

        return FilmDocument.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .description(projection.getDescription())
                .releaseDate(projection.getReleaseDate())
                .duration(projection.getDuration())
                .posterUrl(projection.getPosterUrl())
                .trailerUrl(projection.getTrailerUrl())
                .genres(genres)
                .actors(actors)
                .directors(directors)
                .build();
    }

    @Override
    public String modifyFavoriteFilmList(UUID filmId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String favoriteFilmKey = RedisKey.FAVORITE_FILMS + userId;
        Optional<IFilmElasticsearchProjection> optionalIFilmProjection =
                filmRepository.findFilmDetailsForElasticsearch(filmId);

        if (optionalIFilmProjection.isEmpty()) {
            throw new AppException(ErrorCode.FILM_NOT_FOUND);
        }

        try {
            FilmDocument filmDoc = toFilmDocument(optionalIFilmProjection.get());

            if (!redisService.existsKey(favoriteFilmKey)) {
                // Chưa có danh sách yêu thích → tạo mới
                List<FilmDocument> filmDocuments = new ArrayList<>();

                filmDocuments.add(filmDoc);

                redisService.save(favoriteFilmKey, filmDocuments, SEVEN_DAYS, TimeUnit.MINUTES);

                return "Thêm phim vào danh sách yêu thích thành công";
            } else {
                // Lấy danh sách hiện tại trong Redis
                List<FilmDocument> filmDocuments = (List<FilmDocument>) redisService.get(favoriteFilmKey);
                Long ttl = redisService.getTTL(favoriteFilmKey, TimeUnit.MINUTES);

                if (filmDocuments == null) {
                    filmDocuments = new ArrayList<>();
                }

                boolean alreadyExists =
                        filmDocuments.stream().anyMatch(f -> f.getId().equals(filmDoc.getId()));

                if (alreadyExists) {
                    // Nếu phim đã có trong danh sách → xóa
                    filmDocuments = filmDocuments.stream()
                            .filter(f -> !f.getId().equals(filmDoc.getId()))
                            .collect(Collectors.toList());

                    if (ttl != null && ttl > 0) {
                        redisService.save(favoriteFilmKey, filmDocuments, ttl, TimeUnit.MINUTES);
                    } else {
                        redisService.save(favoriteFilmKey, filmDocuments, SEVEN_DAYS, TimeUnit.MINUTES);
                    }

                    return "Đã xóa phim khỏi danh sách yêu thích";
                } else {
                    // Nếu phim chưa có trong danh sách → thêm mới
                    filmDocuments.add(filmDoc);

                    if (ttl != null && ttl > 0) {
                        redisService.save(favoriteFilmKey, filmDocuments, ttl, TimeUnit.MINUTES);
                    } else {
                        redisService.save(favoriteFilmKey, filmDocuments, SEVEN_DAYS, TimeUnit.MINUTES);
                    }

                    return "Thêm phim vào danh sách yêu thích thành công";
                }
            }
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

    @Override
    public List<FilmDocument> getFavoriteFilmList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String favoriteFilmKey = RedisKey.FAVORITE_FILMS + userId;
        List<FilmDocument> filmDocuments = (List<FilmDocument>) redisService.get(favoriteFilmKey);

        return filmDocuments;
    }
}
