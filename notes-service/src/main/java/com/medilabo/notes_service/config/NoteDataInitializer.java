package com.medilabo.notes_service.config;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.List;

@Configuration
@Profile("docker")
public class NoteDataInitializer {

    @Bean
    CommandLineRunner initializeNotes(NoteRepository noteRepository) {
        return args -> {
            if (noteRepository.count() > 0) {
                return;
            }

            Instant baseTime = Instant.parse("2026-01-01T09:00:00Z");

            noteRepository.saveAll(List.of(
                    createNote(
                            1L,
                            "Le patient déclare qu'il se sent très bien. "
                                    + "Poids égal ou inférieur au poids recommandé.",
                            baseTime.plusSeconds(1)
                    ),
                    createNote(
                            2L,
                            "Le patient déclare qu'il ressent beaucoup de stress au travail. "
                                    + "Il se plaint également que son audition est anormale dernièrement.",
                            baseTime.plusSeconds(2)
                    ),
                    createNote(
                            2L,
                            "Le patient déclare avoir fait une réaction aux médicaments "
                                    + "au cours des 3 derniers mois. "
                                    + "Il remarque également que son audition continue d'être anormale.",
                            baseTime.plusSeconds(3)
                    ),
                    createNote(
                            3L,
                            "Le patient déclare qu'il fume depuis peu.",
                            baseTime.plusSeconds(4)
                    ),
                    createNote(
                            3L,
                            "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer "
                                    + "l'année dernière. Il se plaint également de crises d'apnée "
                                    + "respiratoire anormales. Tests de laboratoire indiquant un taux "
                                    + "de cholestérol LDL élevé.",
                            baseTime.plusSeconds(5)
                    ),
                    createNote(
                            4L,
                            "Le patient déclare qu'il lui est devenu difficile de monter les escaliers. "
                                    + "Il se plaint également d'être essoufflé. Tests de laboratoire "
                                    + "indiquant que les anticorps sont élevés. Réaction aux médicaments.",
                            baseTime.plusSeconds(6)
                    ),
                    createNote(
                            4L,
                            "Le patient déclare qu'il a mal au dos lorsqu'il reste assis "
                                    + "pendant longtemps.",
                            baseTime.plusSeconds(7)
                    ),
                    createNote(
                            4L,
                            "Le patient déclare avoir commencé à fumer depuis peu. "
                                    + "Hémoglobine A1C supérieure au niveau recommandé.",
                            baseTime.plusSeconds(8)
                    ),
                    createNote(
                            4L,
                            "Taille, Poids, Cholestérol, Vertige et Réaction.",
                            baseTime.plusSeconds(9)
                    )
            ));
        };
    }

    private Note createNote(
            Long patientId,
            String text,
            Instant createdAt
    ) {
        Note note = new Note();
        note.setPatientId(patientId);
        note.setText(text);
        note.setCreatedAt(createdAt);
        note.setModifiedAt(createdAt);

        return note;
    }
}