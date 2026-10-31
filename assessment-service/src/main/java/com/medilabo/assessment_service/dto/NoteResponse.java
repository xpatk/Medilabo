package com.medilabo.assessment_service.dto;

import java.time.Instant;

public record NoteResponse(
        String id,
        Long patientId,
        String text,
        Instant createdAt,
        Instant modifiedAt
) {
}