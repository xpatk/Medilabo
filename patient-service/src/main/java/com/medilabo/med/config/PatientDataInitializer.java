package com.medilabo.med.config;

import com.medilabo.med.model.Gender;
import com.medilabo.med.model.Patient;
import com.medilabo.med.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("docker")
public class PatientDataInitializer {

    @Bean
    CommandLineRunner initializePatients(PatientRepository patientRepository) {
        return args -> {
            if (patientRepository.count() > 0) {
                return;
            }

            patientRepository.saveAll(List.of(
                    new Patient(
                            "Test",
                            "TestNone",
                            LocalDate.of(1966, 12, 31),
                            Gender.F,
                            "1 Brookside St",
                            "100-222-3333"
                    ),
                    new Patient(
                            "Test",
                            "TestBorderline",
                            LocalDate.of(1945, 6, 24),
                            Gender.M,
                            "2 High St",
                            "200-333-4444"
                    ),
                    new Patient(
                            "Test",
                            "TestInDanger",
                            LocalDate.of(2004, 6, 18),
                            Gender.M,
                            "3 Club Road",
                            "300-444-5555"
                    ),
                    new Patient(
                            "Test",
                            "TestEarlyOnset",
                            LocalDate.of(2002, 6, 28),
                            Gender.F,
                            "4 Valley Dr",
                            "400-555-6666"
                    )
            ));
        };
    }
}