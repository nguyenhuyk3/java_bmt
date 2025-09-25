package com.bmt.java_bmt.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bmt.java_bmt.dto.others.IFilmElasticsearchProjection;
import com.bmt.java_bmt.entities.Film;

public interface IFilmRepository extends JpaRepository<Film, UUID> {
    //    @Query(
    //            value =
    //                    """
    //				SELECT
    //					CAST(f.f_id AS CHAR) as id, -- Chuyển UUID sang chuỗi
    //					f.f_title as title,
    //					f.f_description as description,
    //					CAST(f.f_release_date AS CHAR) as releaseDate,
    //					CAST(f.f_duration AS CHAR) as duration,
    //					ofi.ofi_poster_url as posterUrl,
    //					ofi.ofi_trailer_url as trailerUrl,
    //					-- Sử dụng JSON_ARRAYAGG cho genres
    //					(SELECT JSON_ARRAYAGG(fg.fg_genre) FROM film_genres fg WHERE fg.f_id = f.f_id) as genres,
    //					-- Sử dụng JSON_OBJECT và JSON_ARRAYAGG cho actors
    //					(
    //						SELECT JSON_ARRAYAGG(JSON_OBJECT('id', CAST(fpf.fpf_id AS CHAR), 'fullName', pi.pi_full_name))
    //						FROM film_people fpp
    //						JOIN film_professionals fpf ON fpp.fpf_id = fpf.fpf_id
    //						JOIN personal_information pi ON fpf.pi_id = pi.pi_id
    //						WHERE fpp.f_id = f.f_id AND fpf.fpf_job = 'actor'
    //					) as actors,
    //					-- Sử dụng JSON_OBJECT và JSON_ARRAYAGG cho directors
    //					(
    //						SELECT JSON_ARRAYAGG(JSON_OBJECT('id', CAST(fpf.fpf_id AS CHAR), 'fullName', pi.pi_full_name))
    //						FROM film_people fpp
    //						JOIN film_professionals fpf ON fpp.fpf_id = fpf.fpf_id
    //						JOIN personal_information pi ON fpf.pi_id = pi.pi_id
    //						WHERE fpp.f_id = f.f_id AND fpf.fpf_job = 'director'
    //					) as directors
    //				FROM
    //					films f
    //				LEFT JOIN
    //					other_film_informations ofi ON f.f_id = ofi.f_id
    //				WHERE
    //					f.f_id = :filmId
    //			""",
    //            nativeQuery = true)
    //    Optional<IFilmElasticsearchProjection> findFilmDetailsForElasticsearch(@Param("filmId") UUID filmId);

    @Query(
            value =
                    """
		SELECT
			BIN_TO_UUID(f.f_id) AS id,
			f.f_title AS title,
			f.f_description AS description,
			CAST(f.f_release_date AS CHAR) AS releaseDate,
			CAST(f.f_duration AS CHAR) AS duration,
			ofi.ofi_poster_url AS posterUrl,
			ofi.ofi_trailer_url AS trailerUrl,

			-- genres
			(SELECT JSON_ARRAYAGG(fg.fg_genre)
			FROM film_genres fg
			WHERE fg.f_id = f.f_id) AS genres,

			-- actors: chỉ lấy fullname
			(SELECT JSON_ARRAYAGG(pi.pi_full_name)
			FROM film_people fpp
			JOIN film_professionals fpf ON fpp.fpf_id = fpf.fpf_id
			JOIN personal_information pi ON fpf.pi_id = pi.pi_id
			WHERE fpp.f_id = f.f_id AND fpf.fpf_job = 'actor') AS actors,

			-- directors: chỉ lấy fullname
			(SELECT JSON_ARRAYAGG(pi.pi_full_name)
			FROM film_people fpp
			JOIN film_professionals fpf ON fpp.fpf_id = fpf.fpf_id
			JOIN personal_information pi ON fpf.pi_id = pi.pi_id
			WHERE fpp.f_id = f.f_id AND fpf.fpf_job = 'director') AS directors

		FROM films f
		LEFT JOIN other_film_informations ofi ON f.f_id = ofi.f_id
		WHERE f.f_id = :filmId
		""",
            nativeQuery = true)
    Optional<IFilmElasticsearchProjection> findFilmDetailsForElasticsearch(@Param("filmId") UUID filmId);

    Page<Film> findAll(Pageable pageable);
}
