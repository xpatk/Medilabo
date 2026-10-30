package com.medilabo.notes_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateNoteRequest (

        @NotBlank(message = "Text is required")
        String text
) {

}
