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

/**
 * Expose les endpoints REST permettant de consulter et d'ajouter
 * des notes médicales pour un patient.
 */
@RestController
@Validated
@RequestMapping("/api/notes/patients/{patientId}")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Retourne l'historique des notes d'un patient.
     *
     * @param patientId identifiant positif du patient
     * @return les notes du patient, de la plus récente à la plus ancienne
     */
    @GetMapping
    public List<Note> getNotesForPatient(
            @PathVariable Long patientId
    ) {
        return noteService.getNotesForPatient(patientId);
    }

    /**
     * Ajoute une nouvelle note de consultation à l'historique d'un patient.
     *
     * @param patientId identifiant positif du patient
     * @param request contenu validé de la note à créer
     * @return la note médicale créée
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.createNote(patientId, request);
    }
}
