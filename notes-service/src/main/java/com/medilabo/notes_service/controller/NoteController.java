package com.medilabo.notes_service.controller;

import com.medilabo.notes_service.dto.CreateNoteRequest;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/notes/patients/{patientId}")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> getNotesForPatient(
            @PathVariable @Positive Long patientId
    ) {
        return noteService.getNotesForPatient(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(
            @PathVariable @Positive Long patientId,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.createNote(patientId, request);
    }
}
