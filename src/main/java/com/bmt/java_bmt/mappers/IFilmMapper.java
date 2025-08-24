package com.bmt.java_bmt.mappers;

import com.bmt.java_bmt.entities.FilmProfessional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.entities.Film;
import com.bmt.java_bmt.entities.OtherFilmInformation;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IFilmMapper {
    Film toFilm(CreateFilmRequest request);

    OtherFilmInformation toOtherFilmInformation(com.bmt.java_bmt.dto.others.OtherFilmInformation requeset);

    @Mapping(source = "changedBy.id", target = "changedBy")
    @Mapping(source = "filmProfessionals", target = "filmProfessionals", qualifiedByName = "mapFilmProfessionalsToUuids")
    @Mapping(source = "otherFilmInformation", target = "otherFilmInformation")
    CreateFilmResponse toCreateFilmResponse(Film request);

    /*
            - Đây là một phương thức mặc định trong interface,
        MapStruct có thể tự động gọi khi cần map Set<FilmProfessional> → Set<UUID>.
     */
    @Named("mapFilmProfessionalsToUuids")
    default Set<UUID> mapFilmProfessionalsToUuids(Set<FilmProfessional> professionals) {
        if (professionals == null) {
            return Set.of();
        }

        return professionals.stream()
                .map(FilmProfessional::getId)
                .collect(Collectors.toSet());
    }
}
