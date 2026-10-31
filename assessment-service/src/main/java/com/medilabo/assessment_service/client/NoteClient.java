package com.medilabo.assessment_service.client;

import com.medilabo.assessment_service.dto.NoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NoteClient {

    private final RestClient restClient;
    private final String notesServiceBaseUrl;

    public NoteClient(
            RestClient restClient,
            @Value("${app.notes-service.base-url}") String notesServiceBaseUrl
    ) {
        this.restClient = restClient;
        this.notesServiceBaseUrl = notesServiceBaseUrl;
    }

    public List<NoteResponse> getNotesForPatient(
            Long patientId,
            String authorizationHeader
    ) {
        List<NoteResponse> notes = restClient.get()
                .uri(
                        notesServiceBaseUrl + "/api/notes/patients/{patientId}",
                        patientId
                )
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NoteResponse>>() {
                });

        return notes == null ? List.of() : notes;
    }
}