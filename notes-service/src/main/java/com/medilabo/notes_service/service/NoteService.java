package com.medilabo.notes_service.service;

import com.medilabo.notes_service.dto.CreateNoteRequest;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Initialise le service de gestion des notes médicales.
     *
     * @param noteRepository accès aux notes stockées dans MongoDB
     */
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retourne les notes médicales associées à un patient.
     *
     * @param patientId identifiant positif du patient
     * @return les notes du patient, triées de la plus récente à la plus ancienne
     * @throws ResponseStatusException avec le statut HTTP 400 si l'identifiant
     *                                est nul ou inférieur ou égal à zéro
     */
    public List<Note> getNotesForPatient(Long patientId) {
        validatePatientId(patientId);

        return noteRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId);
    }

    /**
     * Crée une nouvelle note de consultation pour un patient.
     *
     * @param patientId identifiant positif du patient concerné
     * @param request contenu de la note validé par le contrôleur
     * @return la note enregistrée avec ses dates de création et de modification
     * @throws ResponseStatusException avec le statut HTTP 400 si l'identifiant
     *                                est nul ou inférieur ou égal à zéro
     */
    public Note createNote(Long patientId, CreateNoteRequest request) {
        validatePatientId(patientId);

        Instant now = Instant.now();

        Note note = new Note();
        note.setPatientId(patientId);
        note.setText(request.text());
        note.setCreatedAt(now);
        note.setModifiedAt(now);

        return noteRepository.save(note);
    }

    /**
     * Vérifie que l'identifiant du patient est strictement positif.
     *
     * @param patientId identifiant à vérifier
     */
    private void validatePatientId(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Patient ID must be positive"
            );
        }
    }
}