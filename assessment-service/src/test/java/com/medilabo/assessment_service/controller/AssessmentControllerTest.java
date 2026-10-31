package com.medilabo.assessment_service.controller;

import com.medilabo.assessment_service.dto.AssessmentResponse;
import com.medilabo.assessment_service.model.RiskLevel;
import com.medilabo.assessment_service.service.AssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@WebMvcTest(AssessmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssessmentControllerTest {

    private static final String AUTHORIZATION_HEADER = "Bearer test-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssessmentService assessmentService;

    @Test
    void returnsAssessmentForPatient() throws Exception {
        AssessmentResponse response = new AssessmentResponse(
                1L,
                RiskLevel.NONE
        );

        when(assessmentService.assessPatient(
                1L,
                AUTHORIZATION_HEADER
        )).thenReturn(response);

        mockMvc.perform(
                        get("/api/assessments/patients/1")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        AUTHORIZATION_HEADER
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.riskLevel").value("None"));

        verify(assessmentService).assessPatient(
                1L,
                AUTHORIZATION_HEADER
        );
    }

    @Test
    void rejectsInvalidPatientId() throws Exception {
        mockMvc.perform(
                        get("/api/assessments/patients/0")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        AUTHORIZATION_HEADER
                                )
                )
                .andExpect(status().isBadRequest());
        verifyNoInteractions(assessmentService);
    }
}