package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.client.NoteClient;
import com.medilabo.assessment_service.client.PatientClient;
import com.medilabo.assessment_service.dto.AssessmentResponse;
import com.medilabo.assessment_service.dto.NoteResponse;
import com.medilabo.assessment_service.dto.PatientResponse;
import com.medilabo.assessment_service.model.RiskLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTest {

    private static final String AUTHORIZATION_HEADER = "Bearer test-token";

    @Mock
    private PatientClient patientClient;

    @Mock
    private NoteClient noteClient;

    @InjectMocks
    private AssessmentService assessmentService;

    @Test
    void returnsNoneWhenPatientHasOnlyOneDistinctTrigger() {
        PatientResponse patient = patient(1L, 50, "F");

        List<NoteResponse> notes = List.of(
                note(
                        1L,
                        "Poids égal ou inférieur au poids recommandé."
                )
        );

        AssessmentResponse result = assessPatient(1L, patient, notes);

        assertEquals(RiskLevel.NONE, result.riskLevel());
        verify(patientClient).getPatient(1L, AUTHORIZATION_HEADER);
        verify(noteClient).getNotesForPatient(1L, AUTHORIZATION_HEADER);
    }

    @Test
    void returnsBorderlineForPatientOverThirtyWithTwoDistinctTriggers() {
        PatientResponse patient = patient(2L, 60, "M");

        List<NoteResponse> notes = List.of(
                note(
                        2L,
                        "L'audition est anormale. Réaction aux médicaments."
                )
        );

        AssessmentResponse result = assessPatient(2L, patient, notes);

        assertEquals(RiskLevel.BORDERLINE, result.riskLevel());
    }

    @Test
    void returnsInDangerForManUnderThirtyWithThreeDistinctTriggers() {
        PatientResponse patient = patient(3L, 25, "M");

        List<NoteResponse> notes = List.of(
                note(
                        3L,
                        "Le patient est fumeur. Résultat anormal. Cholestérol élevé."
                )
        );

        AssessmentResponse result = assessPatient(3L, patient, notes);

        assertEquals(RiskLevel.IN_DANGER, result.riskLevel());
    }

    @Test
    void returnsEarlyOnsetForWomanUnderThirtyWithSevenDistinctTriggers() {
        PatientResponse patient = patient(4L, 25, "F");

        List<NoteResponse> notes = List.of(
                note(
                        4L,
                        """
                        Hémoglobine A1C supérieure au niveau recommandé.
                        Taille et poids relevés.
                        Patiente fumeuse.
                        Résultat anormal.
                        Cholestérol élevé.
                        Vertiges récurrents.
                        """
                )
        );

        AssessmentResponse result = assessPatient(4L, patient, notes);

        assertEquals(RiskLevel.EARLY_ONSET, result.riskLevel());
    }

    private AssessmentResponse assessPatient(
            Long patientId,
            PatientResponse patient,
            List<NoteResponse> notes
    ) {
        when(patientClient.getPatient(patientId, AUTHORIZATION_HEADER))
                .thenReturn(patient);

        when(noteClient.getNotesForPatient(patientId, AUTHORIZATION_HEADER))
                .thenReturn(notes);

        return assessmentService.assessPatient(
                patientId,
                AUTHORIZATION_HEADER
        );
    }

    private PatientResponse patient(
            Long id,
            int age,
            String gender
    ) {
        LocalDate birthDate = LocalDate.now()
                .minusYears(age)
                .minusDays(1);

        return new PatientResponse(
                id,
                "Test",
                "Patient",
                birthDate,
                gender,
                null,
                null
        );
    }

    private NoteResponse note(Long patientId, String text) {
        Instant now = Instant.now();

        return new NoteResponse(
                "note-" + patientId,
                patientId,
                text,
                now,
                now
        );
    }
}
