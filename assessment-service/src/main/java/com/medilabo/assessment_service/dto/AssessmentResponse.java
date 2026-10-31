package com.medilabo.assessment_service.dto;

import com.medilabo.assessment_service.model.RiskLevel;

public record AssessmentResponse(
        Long patientId,
        RiskLevel riskLevel
) {
}