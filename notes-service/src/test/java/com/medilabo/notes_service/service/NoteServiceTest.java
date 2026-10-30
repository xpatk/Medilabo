package com.medilabo.notes_service.service;

import com.medilabo.notes_service.dto.CreateNoteRequest;
import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void returnsNotesForPatientFromRepository() {
        Note newestNote = note(
                "note-2",
                4L,
                "Newest note",
                Instant.parse("2026-01-02T09:00:00Z")
        );

        Note oldestNote = note(
                "note-1",
                4L,
                "Oldest note",
                Instant.parse("2026-01-01T09:00:00Z")
        );

        List<Note> notes = List.of(newestNote, oldestNote);

        when(noteRepository.findAllByPatientIdOrderByCreatedAtDesc(4L))
                .thenReturn(notes);

        List<Note> result = noteService.getNotesForPatient(4L);

        assertThat(result).containsExactly(newestNote, oldestNote);

        verify(noteRepository)
                .findAllByPatientIdOrderByCreatedAtDesc(4L);
    }

    @Test
    void createsNoteWithOriginalTextAndTimestamps() {
        String text = "First line.\nSecond line.";

        CreateNoteRequest request = new CreateNoteRequest(text);

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Note result = noteService.createNote(2L, request);

        ArgumentCaptor<Note> noteCaptor =
                ArgumentCaptor.forClass(Note.class);

        verify(noteRepository).save(noteCaptor.capture());

        Note savedNote = noteCaptor.getValue();

        assertThat(savedNote.getPatientId()).isEqualTo(2L);
        assertThat(savedNote.getText()).isEqualTo(text);
        assertThat(savedNote.getCreatedAt()).isNotNull();
        assertThat(savedNote.getModifiedAt())
                .isEqualTo(savedNote.getCreatedAt());

        assertThat(result).isSameAs(savedNote);
    }

    private Note note(
            String id,
            Long patientId,
            String text,
            Instant createdAt
    ) {
        Note note = new Note();

        note.setId(id);
        note.setPatientId(patientId);
        note.setText(text);
        note.setCreatedAt(createdAt);
        note.setModifiedAt(createdAt);

        return note;
    }
}