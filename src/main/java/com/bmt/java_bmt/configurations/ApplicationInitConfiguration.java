package com.bmt.java_bmt.configurations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bmt.java_bmt.dto.others.Id;
import com.bmt.java_bmt.dto.responses.filmProfessional.IFilmProfessionalView;
import com.bmt.java_bmt.entities.*;
import com.bmt.java_bmt.entities.enums.*;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.helpers.constants.Others;
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

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CreateFoodAndBeverageRequest {
        String name;
        FabType type;
        String imageUrl;
        int price;
        boolean isDeleted;
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

    public List<CreateFoodAndBeverageRequest> createFoodAndBeverageRequests() {
        return List.of(
                CreateFoodAndBeverageRequest.builder()
                        .name("Coca-Cola")
                        .type(FabType.BEVERAGE)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1567103472667-6898f3a79cf2?q=80&w=300&auto=format&fit=crop")
                        .price(20000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Bắp rang bơ")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1578849278619-e73505e9610f?q=80&w=300&auto=format&fit=crop")
                        .price(30000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Nacho phô mai")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1513456852971-30c0b8199d4d?q=80&w=300&auto=format&fit=crop")
                        .price(40000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Snack")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1741520149946-d2e652514b5a?q=80&w=300&auto=format&fit=crop")
                        .price(25000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Trà sữa")
                        .type(FabType.BEVERAGE)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1572490122747-3968b75cc699?q=80&w=300&auto=format&fit=crop")
                        .price(35000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Nước khoáng")
                        .type(FabType.BEVERAGE)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1638688569176-5b6db19f9d2a?q=80&w=300&auto=format&fit=crop")
                        .price(15000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Cà phê")
                        .type(FabType.BEVERAGE)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1627261581533-f2357c73c4d0?q=80&w=300&auto=format&fit=crop")
                        .price(30000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Kẹo dẻo")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?q=80&w=300&auto=format&fit=crop")
                        .price(20000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Bánh quy")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?q=80&w=300&auto=format&fit=crop")
                        .price(22000)
                        .isDeleted(false)
                        .build(),
                CreateFoodAndBeverageRequest.builder()
                        .name("Sô cô la")
                        .type(FabType.FOOD)
                        .imageUrl(
                                "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?q=80&w=300&auto=format&fit=crop")
                        .price(28000)
                        .isDeleted(false)
                        .build());
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

        Collections.shuffle(actors);

        List<UUID> randomActors = actors.stream().limit(5).toList();
        Set<FilmProfessional> filmProfessionals = new HashSet<>();

        filmProfessionals.add(professionalRepo
                .findById(randomDirector)
                .orElseThrow(() -> new AppException(ErrorCode.PROFESSIONAL_ID_DOESNT_EXIST)));
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
            Id filmId = Id.builder().id(film.getId().toString()).build();

            outboxRepo.save(Outbox.builder()
                    .eventType(Others.FILM_CREATED)
                    .payload(objectMapper.writeValueAsString(filmId))
                    .build());
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

    private void createFilms(
            UUID managerId,
            IUserRepository userRepo,
            IFilmRepository filmRepo,
            IFilmProfessionalRepository filmProfessionalRepo,
            IOutboxRepository outboxRepo,
            ObjectMapper objectMapper) {
        if (filmRepo.count() != 0) {
            return;
        }

        List<IFilmProfessionalView> allIdsAndJobs = filmProfessionalRepo.findAllIdAndJobs();
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
                    userRepo.findById(managerId).orElseThrow(() -> new AppException(ErrorCode.USER_ID_DOESNT_EXIST)),
                    directors,
                    actors,
                    filmProfessionalRepo,
                    filmRepo);

            saveOutboxEvent(savedFilm, outboxRepo, objectMapper);
        }
    }

    private void createFoodAndBeverage(CreateFoodAndBeverageRequest req, IFoodAndBeverageRepository fabRepo) {
        fabRepo.save(FoodAndBeverage.builder()
                .name(req.getName())
                .type(req.type)
                .imageUrl(req.getImageUrl())
                .price(req.getPrice())
                .isDeleted(false)
                .build());
    }

    private void createFoodsAndBeverage(IFoodAndBeverageRepository fabRepo) {
        if (fabRepo.count() == 0) {
            List<CreateFoodAndBeverageRequest> createFoodAndBeverageRequests = createFoodAndBeverageRequests();

            for (CreateFoodAndBeverageRequest req : createFoodAndBeverageRequests) {
                createFoodAndBeverage(req, fabRepo);
            }
        }
    }

    private int getPriceBySeatType(SeatType type) {
        return switch (type) {
            case STANDARD -> 50000;
            case COUPLED -> 80000;
            case VIP -> 100000;
        };
    }

    private void createSeats(
            ICinemaRepository cinemaRepo, IAuditoriumRepository auditoriumRepo, ISeatRepository seatRepo) {
        if (cinemaRepo.count() != 0) {
            return;
        }

        for (City city : City.values()) {
            for (int i = 1; i <= 3; i++) {
                Cinema cinema = Cinema.builder()
                        .name(city.name() + " Cinema " + i)
                        .city(city)
                        .location("Địa chỉ " + i + " tại " + city.name())
                        .isReleased(true)
                        .build();

                cinemaRepo.save(cinema);

                // Tạo 5 auditorium cho mỗi cinema
                for (int j = 1; j <= 5; j++) {
                    Auditorium auditorium = Auditorium.builder()
                            .name("Auditorium " + j)
                            .seatCapacity(80)
                            .isReleased(true)
                            .cinema(cinema)
                            .build();

                    auditoriumRepo.save(auditorium);

                    // Tạo 80 ghế cho mỗi auditorium
                    for (int k = 1; k <= 80; k++) {
                        SeatType seatType;

                        if (k <= 40) {
                            seatType = SeatType.STANDARD;
                        } else if (k <= 60) {
                            seatType = SeatType.COUPLED;
                        } else {
                            seatType = SeatType.VIP;
                        }

                        int rowIndex = (k - 1) / 10; // 0 -> A, 1 -> B, ...
                        char rowLetter = (char) ('A' + rowIndex);
                        int seatNumberInRow = (k - 1) % 10 + 1;
                        String seatNumber = rowLetter + String.valueOf(seatNumberInRow);
                        Seat seat = Seat.builder()
                                .seatNumber(seatNumber)
                                .seatType(seatType)
                                .price(getPriceBySeatType(seatType))
                                .auditorium(auditorium)
                                .build();

                        seatRepo.save(seat);
                    }
                }
            }
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
            IFoodAndBeverageRepository foodAndBeverageRepository,
            ICinemaRepository cinemaRepository,
            IAuditoriumRepository auditoriumRepository,
            ISeatRepository seatRepository,
            ObjectMapper objectMapper,
            PasswordEncoder passwordEncoder) {
        return args -> {
            UUID managerId = createManagerIfNotExists(userRepository, passwordEncoder);

            createProfessionalsIfNeeded(personalInformationRepository, filmProfessionalRepository);
            createFilms(
                    managerId,
                    userRepository,
                    filmRepository,
                    filmProfessionalRepository,
                    outboxRepository,
                    objectMapper);
            createFoodsAndBeverage(foodAndBeverageRepository);
            createSeats(cinemaRepository, auditoriumRepository, seatRepository);
        };
    }
}
