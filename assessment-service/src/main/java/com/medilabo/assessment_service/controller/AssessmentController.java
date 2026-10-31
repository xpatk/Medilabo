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

@RestController
@RequestMapping("/api/assessments")
@Validated
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

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