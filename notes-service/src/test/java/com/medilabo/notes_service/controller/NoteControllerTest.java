package com.medilabo.notes_service.controller;

import com.medilabo.notes_service.model.Note;
import com.medilabo.notes_service.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    void returnsNotesForPatientAsJson() throws Exception {
        Note note = note(
                "note-1",
                4L,
                "Taille, Poids, Cholestérol, Vertige et Réaction.",
                Instant.parse("2026-01-01T09:00:00Z")
        );

        when(noteService.getNotesForPatient(4L))
                .thenReturn(List.of(note));

        mockMvc.perform(get("/api/notes/patients/{patientId}", 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("note-1"))
                .andExpect(jsonPath("$[0].patientId").value(4))
                .andExpect(jsonPath("$[0].text")
                        .value("Taille, Poids, Cholestérol, Vertige et Réaction."));

        verify(noteService).getNotesForPatient(4L);
    }

    @Test
    void createsNoteAndReturnsCreatedStatus() throws Exception {
        Note createdNote = note(
                "note-2",
                2L,
                "First line.\nSecond line.",
                Instant.parse("2026-01-02T09:00:00Z")
        );

        when(noteService.createNote(eq(2L), any()))
                .thenReturn(createdNote);

        mockMvc.perform(post("/api/notes/patients/{patientId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "First line.\\nSecond line."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("note-2"))
                .andExpect(jsonPath("$.patientId").value(2))
                .andExpect(jsonPath("$.text")
                        .value("First line.\nSecond line."));

        verify(noteService).createNote(eq(2L), any());
    }

    @Test
    void returnsBadRequestWhenNoteTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/notes/patients/{patientId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
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