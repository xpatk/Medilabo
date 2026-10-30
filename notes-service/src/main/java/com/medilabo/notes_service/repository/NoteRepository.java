package com.medilabo.notes_service.repository;

import com.medilabo.notes_service.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findAllByPatientIdOrderByCreatedAtDesc(Long patientId);
}
