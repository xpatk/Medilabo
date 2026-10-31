package com.medilabo.assessment_service.service;

import com.medilabo.assessment_service.client.NoteClient;
import com.medilabo.assessment_service.client.PatientClient;
import com.medilabo.assessment_service.dto.AssessmentResponse;
import com.medilabo.assessment_service.dto.NoteResponse;
import com.medilabo.assessment_service.dto.PatientResponse;
import com.medilabo.assessment_service.model.RiskLevel;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class AssessmentService {

    private static final List<Pattern> TRIGGER_PATTERNS = List.of(
            Pattern.compile("\\bhemoglobine\\s+a1c\\b"),
            Pattern.compile("\\bmicroalbumine\\b"),
            Pattern.compile("\\btaille\\b"),
            Pattern.compile("\\bpoids\\b"),
            Pattern.compile("\\bfumeur\\b"),
            Pattern.compile("\\bfumeuse\\b"),
            Pattern.compile("\\banormal\\w*\\b"),
            Pattern.compile("\\bcholesterol\\b"),
            Pattern.compile("\\bvertig\\w*\\b"),
            Pattern.compile("\\brechute\\b"),
            Pattern.compile("\\breaction\\b"),
            Pattern.compile("\\banticorps\\b")
    );

    private final PatientClient patientClient;
    private final NoteClient noteClient;

    public AssessmentService(
            PatientClient patientClient,
            NoteClient noteClient
    ) {
        this.patientClient = patientClient;
        this.noteClient = noteClient;
    }

    public AssessmentResponse assessPatient(
            Long patientId,
            String authorizationHeader
    ) {
        PatientResponse patient = patientClient.getPatient(
                patientId,
                authorizationHeader
        );

        List<NoteResponse> notes = noteClient.getNotesForPatient(
                patientId,
                authorizationHeader
        );

        int triggerCount = countTriggers(notes);
        RiskLevel riskLevel = determineRiskLevel(patient, triggerCount);

        return new AssessmentResponse(patient.id(), riskLevel);
    }

    private int countTriggers(List<NoteResponse> notes) {
        boolean[] foundTriggers = new boolean[TRIGGER_PATTERNS.size()];

        for (NoteResponse note : notes) {
            String text = note.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            String normalizedText = normalize(text);

            for (int index = 0; index < TRIGGER_PATTERNS.size(); index++) {
                Pattern triggerPattern = TRIGGER_PATTERNS.get(index);

                if (triggerPattern.matcher(normalizedText).find()) {
                    foundTriggers[index] = true;
                }
            }
        }

        int triggerCount = 0;

        for (boolean foundTrigger : foundTriggers) {
            if (foundTrigger) {
                triggerCount++;
            }
        }

        return triggerCount;
    }

    private String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private RiskLevel determineRiskLevel(
            PatientResponse patient,
            int triggerCount
    ) {
        if (triggerCount == 0) {
            return RiskLevel.NONE;
        }

        int age = Period.between(
                patient.birthDate(),
                LocalDate.now()
        ).getYears();

        if (age > 30) {
            if (triggerCount >= 8) {
                return RiskLevel.EARLY_ONSET;
            }

            if (triggerCount >= 6) {
                return RiskLevel.IN_DANGER;
            }

            if (triggerCount >= 2) {
                return RiskLevel.BORDERLINE;
            }

            return RiskLevel.NONE;
        }

        boolean isMale = "M".equalsIgnoreCase(patient.gender());

        if (isMale) {
            if (triggerCount >= 5) {
                return RiskLevel.EARLY_ONSET;
            }

            if (triggerCount >= 3) {
                return RiskLevel.IN_DANGER;
            }

            return RiskLevel.NONE;
        }

        if (triggerCount >= 7) {
            return RiskLevel.EARLY_ONSET;
        }

        if (triggerCount >= 4) {
            return RiskLevel.IN_DANGER;
        }

        return RiskLevel.NONE;
    }
}