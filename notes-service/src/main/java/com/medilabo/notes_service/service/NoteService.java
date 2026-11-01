package com.medilabo.notes_service.service;


import com.medilabo.notes_service.dto.CreateNoteRequest;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Gère l'historique des notes médicales associées aux patients.
 *
 * <p>Les notes sont stockées dans MongoDB et retournées de la plus récente
 * à la plus ancienne.</p>
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retourne les notes médicales associées à un patient.
     *
     * @param patientId identifiant du patient
     * @return les notes du patient, triées de la plus récente à la plus ancienne
     */
    public List<Note> getNotesForPatient(Long patientId) {
        return noteRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId);
    }

    /**
     * Crée une nouvelle note de consultation pour un patient.
     *
     * @param patientId identifiant du patient concerné
     * @param request contenu validé de la note à enregistrer
     * @return la note enregistrée avec ses dates de création et de modification
     */
    public Note createNote(Long patientId, CreateNoteRequest request) {
        Instant now = Instant.now();

        Note note = new Note();
        note.setPatientId(patientId);
        note.setText(request.text());
        note.setCreatedAt(now);
        note.setModifiedAt(now);

        return noteRepository.save(note);
    }
}
