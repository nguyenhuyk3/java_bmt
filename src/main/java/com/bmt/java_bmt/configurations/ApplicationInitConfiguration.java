package com.bmt.java_bmt.configurations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bmt.java_bmt.dto.others.FilmId;
import com.bmt.java_bmt.dto.responses.filmProfessional.IFilmProfessionalView;
import com.bmt.java_bmt.entities.*;
import com.bmt.java_bmt.entities.enums.*;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
import com.bmt.java_bmt.mappers.IFilmMapper;
import com.bmt.java_bmt.repositories.*;
import com.bmt.java_bmt.utils.Generator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfiguration {
    @Value("${manager.email}")
    @NonFinal
    String SECRET_MANAGER_EMAIL;

    @Value("${manager.password}")
    @NonFinal
    String SECRET_MANAGER_PASSWORD;

    private final Random random = new Random();

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateFilmRequest {
        String title;
        String description;
        LocalDate releaseDate;
        LocalTime duration;
        Set<Genre> genres;
    }

    private List<CreateFilmRequest> createFilmRequests() {
        return List.of(
                new CreateFilmRequest(
                        "The Journey Begins",
                        "Một câu chuyện phiêu lưu kỳ thú của một nhóm bạn trẻ khám phá thế giới kỳ bí với nhiều thử thách.",
                        LocalDate.of(2020, 5, 20),
                        LocalTime.of(2, 15),
                        Set.of(Genre.ACTION, Genre.ADVENTURE, Genre.ANIMATION, Genre.HISTORICAL)),
                new CreateFilmRequest(
                        "Love in the Rain",
                        "Một câu chuyện tình cảm lãng mạn giữa hai người trẻ tuổi gặp nhau trong những cơn mưa của thành phố.",
                        LocalDate.of(2021, 9, 10),
                        LocalTime.of(1, 45),
                        Set.of(Genre.ROMANCE, Genre.DRAMA, Genre.HORROR, Genre.WESTERN)),
                new CreateFilmRequest(
                        "Future War",
                        "Một bộ phim khoa học viễn tưởng kể về cuộc chiến khốc liệt giữa con người và trí tuệ nhân tạo trong tương lai.",
                        LocalDate.of(2022, 3, 5),
                        LocalTime.of(2, 30),
                        Set.of(Genre.SCI_FI, Genre.ACTION, Genre.DARK_COMEDY, Genre.DOCUMENTARY)),
                new CreateFilmRequest(
                        "The Silent Forest",
                        "Một bộ phim kinh dị kể về nhóm bạn đi lạc trong khu rừng bị nguyền rủa và phải đối mặt với những điều ghê rợn.",
                        LocalDate.of(2019, 10, 31),
                        LocalTime.of(1, 55),
                        Set.of(Genre.HORROR, Genre.MYSTERY, Genre.THRILLER, Genre.MUSICAL)),
                new CreateFilmRequest(
                        "Dreamers",
                        "Bộ phim kể về hành trình của những người trẻ dám mơ ước và theo đuổi đam mê của mình bất chấp khó khăn thử thách.",
                        LocalDate.of(2023, 6, 15),
                        LocalTime.of(2, 0),
                        Set.of(Genre.DRAMA, Genre.SUPERHERO, Genre.SLASHER, Genre.FAMILY)));
    }

    private UUID createManagerIfNotExists(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return userRepository.findByEmail(SECRET_MANAGER_EMAIL).map(User::getId).orElseGet(() -> {
            var manager = User.builder()
                    .email(SECRET_MANAGER_EMAIL)
                    .password(passwordEncoder.encode(SECRET_MANAGER_PASSWORD))
                    .role(Role.MANAGER)
                    .source(Source.APP)
                    .personalInformation(PersonalInformation.builder()
                            .fullName("Manager")
                            .dateOfBirth(LocalDate.parse("1999-08-21"))
                            .sex(Sex.MALE)
                            .avatarUrl("NONE")
                            .build())
                    .build();

            return userRepository.save(manager).getId();
        });
    }

    private void createProfessionalsIfNeeded(
            IPersonalInformationRepository personalInfoRepo, IFilmProfessionalRepository professionalRepo) {
        if (professionalRepo.count() >= 30) {
            return;
        }

        List<FilmProfessional> professionals = new ArrayList<>();

        // 10 directors
        for (int i = 1; i <= 10; i++) {
            PersonalInformation pi = personalInfoRepo.save(PersonalInformation.builder()
                    .fullName(Generator.generateRandomString(8) + " Director " + i)
                    .dateOfBirth(LocalDate.of(1980, 1, i))
                    .sex(Sex.MALE)
                    .avatarUrl("NONE")
                    .build());
            professionals.add(FilmProfessional.builder()
                    .personalInformation(pi)
                    .nationality("Vietnam")
                    .job(Job.DIRECTOR)
                    .build());
        }

        // 20 actors
        for (int i = 1; i <= 20; i++) {
            PersonalInformation pi = personalInfoRepo.save(PersonalInformation.builder()
                    .fullName(Generator.generateRandomString(8) + " Actor " + i)
                    .dateOfBirth(LocalDate.of(1990, 2, (i % 28) + 1))
                    .sex(Sex.FEMALE)
                    .avatarUrl("NONE")
                    .build());
            professionals.add(FilmProfessional.builder()
                    .personalInformation(pi)
                    .nationality("Vietnam")
                    .job(Job.ACTOR)
                    .build());
        }

        professionalRepo.saveAll(professionals);
    }

    private Film createFilm(
            CreateFilmRequest req,
            User manager,
            List<UUID> directors,
            List<UUID> actors,
            IFilmProfessionalRepository professionalRepo,
            IFilmRepository filmRepo) {
        Film film = new Film();

        film.setTitle(req.getTitle());
        film.setDescription(req.getDescription());
        film.setReleaseDate(req.getReleaseDate());
        film.setDuration(req.getDuration());
        film.setGenres(req.getGenres());
        film.setChangedBy(manager);

        filmRepo.saveAndFlush(film);

        // Attach other info
        OtherFilmInformation otherInfo = new OtherFilmInformation();

        otherInfo.setFilm(film);
        otherInfo.setTrailerUrl("NONE");
        otherInfo.setPosterUrl("NONE");

        // Random director + 5 random actors
        UUID randomDirector = directors.get(random.nextInt(directors.size()));

        log.info("sldjflksjfk {}", randomDirector);
        Collections.shuffle(actors);

        List<UUID> randomActors = actors.stream().limit(5).toList();

        log.info("1111111111111111111111111111 {}", randomActors);
        Set<FilmProfessional> filmProfessionals = new HashSet<>();

        filmProfessionals.add(professionalRepo
                .findById(randomDirector)
                .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)));
        log.info("222222222222222222222222222222222222 {}", randomActors);
        filmProfessionals.addAll(randomActors.stream()
                .map(id -> professionalRepo
                        .findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)))
                .toList());

        film.setFilmProfessionals(filmProfessionals);

        return filmRepo.save(film);
    }

    private void saveOutboxEvent(Film film, IOutboxRepository outboxRepo, ObjectMapper objectMapper) {
        try {
            FilmId filmId = FilmId.builder().filmId(film.getId().toString()).build();

            outboxRepo.save(Outbox.builder()
                    .eventType(Others.FILM_CREATED)
                    .payload(objectMapper.writeValueAsString(filmId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

    @Transactional
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    public ApplicationRunner applicationRunner(
            IUserRepository userRepository,
            IPersonalInformationRepository personalInformationRepository,
            IFilmProfessionalRepository filmProfessionalRepository,
            IFilmRepository filmRepository,
            IOutboxRepository outboxRepository,
            IFilmMapper filmMapper,
            ObjectMapper objectMapper,
            PasswordEncoder passwordEncoder) {
        return args -> {
            UUID managerId = createManagerIfNotExists(userRepository, passwordEncoder);

            createProfessionalsIfNeeded(personalInformationRepository, filmProfessionalRepository);

            List<IFilmProfessionalView> allIdsAndJobs = filmProfessionalRepository.findAllIdAndJobs();
            List<UUID> directors = allIdsAndJobs.stream()
                    .filter(fp -> fp.getJob() == Job.DIRECTOR)
                    .map(IFilmProfessionalView::getId)
                    .toList();
            List<UUID> actors = allIdsAndJobs.stream()
                    .filter(fp -> fp.getJob() == Job.ACTOR)
                    .map(IFilmProfessionalView::getId)
                    .collect(Collectors.toList());
            List<CreateFilmRequest> createFilmRequests = createFilmRequests();

            for (CreateFilmRequest req : createFilmRequests) {
                Film savedFilm = createFilm(
                        req,
                        userRepository
                                .findById(managerId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST)),
                        directors,
                        actors,
                        filmProfessionalRepository,
                        filmRepository);

                saveOutboxEvent(savedFilm, outboxRepository, objectMapper);
            }
        };
    }
}
