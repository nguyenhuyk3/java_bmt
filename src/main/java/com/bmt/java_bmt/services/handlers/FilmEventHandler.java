package com.bmt.java_bmt.services.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.others.IFilmElasticsearchProjection;
import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.others.SimplePersonInformation;
import com.bmt.java_bmt.repositories.IFilmRepository;
import com.bmt.java_bmt.services.ISearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmEventHandler {
    IFilmRepository filmRepository;
    ISearchService searchService;
    ObjectMapper objectMapper;

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

    public void handleFilmCreatedAndUpdated(JsonNode afterNode, boolean isUpdated) {
        JsonNode aggregatePayloadNode = afterNode.get("os_payload");

        if (aggregatePayloadNode == null || aggregatePayloadNode.isNull()) {
            log.error("❌ Trong trường 'after' không chứa 'os_payload', bỏ qua");
            return;
        }

        try {
            String aggregatePayloadString = aggregatePayloadNode.asText();
            JsonNode finalPayload = objectMapper.readTree(aggregatePayloadString);
            Id filmId = objectMapper.treeToValue(finalPayload, Id.class);
            UUID filmUuid = UUID.fromString(filmId.getId());
            // Bước 1: Query CSDL để lấy dữ liệu tổng hợp
            Optional<IFilmElasticsearchProjection> projectionOpt =
                    filmRepository.findFilmDetailsForElasticsearch(filmUuid);

            if (projectionOpt.isEmpty()) {
                log.error("⚠️ Không tìm thấy thông tin phim với ID {} trong CSDL. Có thể đã bị xóa.", filmId);
                // Có thể gửi một lệnh xóa tới Elasticsearch ở đây nếu cần
                return;
            }

            // Bước 2: Chuyển đổi projection thành FilmDocument
            FilmDocument filmDocument = toFilmDocument(projectionOpt.get());
            // Bước 3: Đẩy dữ liệu vào Elasticsearch

            if (isUpdated) {
                searchService.deleteFilm(filmUuid.toString());
            }

            searchService.indexFilm(filmDocument);
        } catch (JsonProcessingException e) {
            log.error("❌ Lỗi khi parse 'os_payload' thành filmId: {}", e.getMessage());
        }
    }
}
