package com.medilabo.assessment_service.client;

import com.medilabo.assessment_service.dto.PatientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PatientClient {

    private final RestClient restClient;
    private final String patientServiceBaseUrl;

    public PatientClient(
            RestClient restClient,
            @Value("${app.patient-service.base-url}") String patientServiceBaseUrl
    ) {
        this.restClient = restClient;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    public PatientResponse getPatient(
            Long patientId,
            String authorizationHeader
    ) {
        return restClient.get()
                .uri(
                        patientServiceBaseUrl + "/api/patients/{patientId}",
                        patientId
                )
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .body(PatientResponse.class);
    }
}