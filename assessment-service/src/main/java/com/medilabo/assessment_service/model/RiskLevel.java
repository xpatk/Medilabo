package com.medilabo.assessment_service.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskLevel {

    NONE("None"),
    BORDERLINE("Borderline"),
    IN_DANGER("In Danger"),
    EARLY_ONSET("Early onset");

    private final String label;

    RiskLevel(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}