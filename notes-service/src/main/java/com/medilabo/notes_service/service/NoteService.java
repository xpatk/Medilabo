package com.medilabo.notes_service.service;


import com.medilabo.notes_service.dto.CreateNoteRequest;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesForPatient(Long patientId) {
        return noteRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId);
    }

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
