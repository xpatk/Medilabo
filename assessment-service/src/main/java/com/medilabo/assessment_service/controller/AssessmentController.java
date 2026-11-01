package com.medilabo.assessment_service.controller;

import com.medilabo.assessment_service.dto.AssessmentResponse;
import com.medilabo.assessment_service.service.AssessmentService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose l'endpoint REST permettant d'évaluer le risque de diabète
 * d'un patient.
 */
@RestController
@RequestMapping("/api/assessments")
@Validated
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /**
     * Retourne le niveau de risque de diabète du patient demandé.
     *
     * <p>Le jeton JWT reçu est transmis à l'Assessment Service afin qu'il
     * puisse appeler de manière sécurisée les services Patient et Notes.</p>
     *
     * @param patientId identifiant positif du patient à évaluer
     * @param authorizationHeader en-tête HTTP contenant le jeton JWT
     * @return résultat contenant l'identifiant du patient et son niveau de risque
     */
    @GetMapping("/patients/{patientId}")
    public AssessmentResponse assessPatient(
            @PathVariable @Positive Long patientId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        return assessmentService.assessPatient(
                patientId,
                authorizationHeader
        );
    }
}