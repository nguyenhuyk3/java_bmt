package com.bmt.java_bmt.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.services.ISearchService;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchImpl implements ISearchService {
    @Value("${elastic-search.indexes.index-of-films}")
    @NonFinal
    String INDEX_OF_FILMS;

    ElasticsearchClient elasticsearchClient;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FilmDocument> searchFilms(String userQuery, int from, int size) throws IOException {
        // 1. Xây dựng câu truy vấn multi_match
        Query multiMatchQuery = MultiMatchQuery.of(
                        m -> m.query(userQuery)
                                .fields(
                                        "title^3",
                                        "description",
                                        "genres",
                                        "actors.full_name^2",
                                        "directors.full_name^2")
                        // .fuzziness("AUTO") // Bật tính năng tìm kiếm mờ
                        )
                ._toQuery();
        // 2. Tạo Search Request hoàn chỉnh
        SearchRequest searchRequest = SearchRequest.of(
                s -> s.index(INDEX_OF_FILMS).query(multiMatchQuery).from(from).size(size));
        // 3. Thực thi truy vấn
        SearchResponse<FilmDocument> response = elasticsearchClient.search(searchRequest, FilmDocument.class);
        // 4. Xử lý kết quả
        List<FilmDocument> results = new ArrayList<>();

        for (Hit<FilmDocument> hit : response.hits().hits()) {
            results.add(hit.source());
        }

        return results;
    }
}
