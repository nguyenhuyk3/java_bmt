package com.bmt.java_bmt.configurations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bmt.java_bmt.entities.FilmProfessional;
import com.bmt.java_bmt.entities.PersonalInformation;
import com.bmt.java_bmt.entities.User;
import com.bmt.java_bmt.entities.enums.Job;
import com.bmt.java_bmt.entities.enums.Role;
import com.bmt.java_bmt.entities.enums.Sex;
import com.bmt.java_bmt.entities.enums.Source;
import com.bmt.java_bmt.repositories.IFilmProfessionalRepository;
import com.bmt.java_bmt.repositories.IPersonalInformationRepository;
import com.bmt.java_bmt.repositories.IUserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    public ApplicationRunner applicationRunner(
            IUserRepository userRepository,
            IPersonalInformationRepository personalInformationRepository,
            IFilmProfessionalRepository filmProfessionalRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail(SECRET_MANAGER_EMAIL)) {
                userRepository.save(User.builder()
                        .email(SECRET_MANAGER_EMAIL)
                        .password(passwordEncoder.encode(SECRET_MANAGER_PASSWORD))
                        .role(Role.MANAGER)
                        .source(Source.APP)
                        .personalInformation(PersonalInformation.builder()
                                .firstName("First")
                                .lastName("Manager")
                                .dateOfBirth(LocalDate.parse("1999-08-21"))
                                .sex(Sex.MALE)
                                .avatarUrl("NONE")
                                .build())
                        .build());
            }

            if (filmProfessionalRepository.count() < 30) {
                List<FilmProfessional> filmProfessionalList = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    PersonalInformation personalInformation =
                            personalInformationRepository.save(PersonalInformation.builder()
                                    .firstName("DirectorFirst " + i)
                                    .lastName("DirectorLast " + i)
                                    .dateOfBirth(LocalDate.of(1980, 1, i))
                                    .sex(Sex.MALE)
                                    .avatarUrl("NONE")
                                    .build());
                    FilmProfessional filmProfessional = FilmProfessional.builder()
                            .personalInformation(personalInformation)
                            .nationality("Vietnam")
                            .job(Job.DIRECTOR)
                            .build();

                    filmProfessionalList.add(filmProfessional);
                }

                for (int i = 1; i <= 20; i++) {
                    PersonalInformation personalInformation =
                            personalInformationRepository.save(PersonalInformation.builder()
                                    .firstName("ActorFirst " + i)
                                    .lastName("ActorLast " + i)
                                    .dateOfBirth(LocalDate.of(1990, 2, (i % 28) + 1))
                                    .sex(Sex.FEMALE)
                                    .avatarUrl("NONE")
                                    .build());
                    FilmProfessional filmProfessional = FilmProfessional.builder()
                            .personalInformation(personalInformation)
                            .nationality("Vietnam")
                            .job(Job.ACTOR)
                            .build();

                    filmProfessionalList.add(filmProfessional);
                }

                filmProfessionalRepository.saveAll(filmProfessionalList);
            }
        };
    }
}
