package com.bmt.java_bmt.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.requests.search.SearchRequest;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.services.ISearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SearchImpl implements ISearchService {
    @Value("${elastic-search.indexes.index-of-films}")
    @NonFinal
    String INDEX_OF_FILMS;

    ElasticsearchClient elasticsearchClient;

    //    @Override
    //    public List<FilmDocument> searchFilms(String userQuery, int from, int size) throws IOException {
    //        // 1. Xây dựng câu truy vấn multi_match
    //        Query multiMatchQuery = MultiMatchQuery.of(
    //                        m -> m.query(userQuery)
    //                                .fields(
    //                                        "title^3",
    //                                        "description",
    //                                        "genres",
    //                                        "actors.full_name^2",
    //                                        "directors.full_name^2")
    //                        // .fuzziness("AUTO") // Bật tính năng tìm kiếm mờ
    //                        )
    //                ._toQuery();
    //        // 2. Tạo Search Request hoàn chỉnh
    //        SearchRequest searchRequest = SearchRequest.of(
    //                s -> s.index(INDEX_OF_FILMS).query(multiMatchQuery).from(from).size(size));
    //        // 3. Thực thi truy vấn
    //        SearchResponse<FilmDocument> response = elasticsearchClient.search(searchRequest, FilmDocument.class);
    //        // 4. Xử lý kết quả
    //        List<FilmDocument> results = new ArrayList<>();
    //
    //        for (Hit<FilmDocument> hit : response.hits().hits()) {
    //            results.add(hit.source());
    //        }
    //
    //        return results;
    //    }

    @Override
    public List<FilmDocument> searchFilms(SearchRequest request) {
        // 1. Tạo một BoolQuery.Builder để xây dựng query động
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        // 2. Xử lý tìm kiếm toàn văn bản (trên title và description)
        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            boolQueryBuilder.must(q -> q.multiMatch(
                    mm -> mm.query(request.getQuery()).fields("title", "description") // Tìm trên cả 2 trường
                    ));
        }
        // 3. Xử lý lọc theo thể loại (genres)
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            // Dùng 'filter' vì nó hiệu quả hơn cho việc lọc chính xác
            boolQueryBuilder.filter(f -> f.terms(t -> t.field("genres")
                    .terms(ts -> ts.value(request.getGenres().stream()
                            .map(v -> FieldValue.of(v))
                            .collect(Collectors.toList())))));
        }
        // 4. Xử lý tìm kiếm theo tên diễn viên (nested query)
        if (request.getActorName() != null && !request.getActorName().isBlank()) {
            // Cần dùng nested query vì 'actors' là một mảng các đối tượng
            Query nestedActorQuery = NestedQuery.of(nq -> nq.path("actors") // Đường dẫn đến trường nested
                            .query(q -> q.match(m -> m.field("actors.fullName") // Query bên trong nested object
                                    .query(request.getActorName()))))
                    ._toQuery();

            boolQueryBuilder.filter(nestedActorQuery); // Thêm vào filter
        }
        // 5. Xử lý tìm kiếm theo tên đạo diễn (nested query)
        if (request.getDirectorName() != null && !request.getDirectorName().isBlank()) {
            Query nestedDirectorQuery = NestedQuery.of(nq -> nq.path("directors")
                            .query(q ->
                                    q.match(m -> m.field("directors.fullName").query(request.getDirectorName()))))
                    ._toQuery();

            boolQueryBuilder.filter(nestedDirectorQuery); // Thêm vào filter
        }

        SearchResponse<FilmDocument> response = null;

        try {

            response = elasticsearchClient.search(
                    s -> s.index(INDEX_OF_FILMS)
                            .query(
                                    q -> q.bool(boolQueryBuilder.build()) // Đưa bool query đã xây dựng vào
                                    )
                            .from(0) // Phân trang: bắt đầu từ document 0
                            .size(10), // Phân trang: lấy 10 document
                    FilmDocument.class // Class để map kết quả trả về
                    );
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new AppException(ErrorCode.ELASTICSEARCH_SEARCH_IO_EXCEPTION);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void indexFilm(FilmDocument filmDocument) {
        try {
            IndexResponse response =
                    elasticsearchClient.index(i -> i.index(INDEX_OF_FILMS) // Tên index của bạn, ví dụ "films_index"
                            .id(filmDocument.getId()) // Dùng ID của phim làm ID của document
                            .document(filmDocument));

            log.info("✅ Lập chỉ mục phim thành công. ID: {}, Version: {}", response.id(), response.version());
        } catch (Exception e) {
            log.error("❌ Lỗi khi lập chỉ mục phim ID {}: {}", filmDocument.getId(), e.getMessage());

            // Có thể ném một exception tùy chỉnh ở đây để Kafka consumer biết và retry
            //            throw new RuntimeException("Failed to index film", e);
        }
    }
}
