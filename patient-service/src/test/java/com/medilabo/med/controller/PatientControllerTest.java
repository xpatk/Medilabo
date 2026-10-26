package com.medilabo.med.controller;

import com.medilabo.med.exception.PatientNotFoundException;
import com.medilabo.med.model.Gender;
import com.medilabo.med.model.Patient;
import com.medilabo.med.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void returnsAllPatientsAsJson() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(
                patient("June", "Doe", Gender.F),
                patient("Alain", "Tester", Gender.M)
        ));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("June"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].firstName").value("Alain"));

        verify(patientService).getAllPatients();
    }

    @Test
    void returnsPatientByIdAsJson() throws Exception {
        when(patientService.getPatientById(1L))
                .thenReturn(patient("Ada", "Lovelace", Gender.F));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.lastName").value("Lovelace"))
                .andExpect(jsonPath("$.gender").value("F"));

        verify(patientService).getPatientById(1L);
    }

    @Test
    void returnsNotFoundWhenPatientDoesNotExist() throws Exception {
        when(patientService.getPatientById(99L))
                .thenThrow(new PatientNotFoundException(99L));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient was not found."));

        verify(patientService).getPatientById(99L);
    }

    @Test
    void returnsBadRequestWhenRequiredPatientDataIsMissing() throws Exception {
        String invalidPatientJson = """
                {
                  "lastName": "Doe",
                  "birthDate": "1990-01-01",
                  "gender": "F"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName")
                        .value("First name is required"));

        verifyNoInteractions(patientService);
    }

    @Test
    void createsPatientAndReturnsCreatedStatus() throws Exception {
        Patient savedPatient = patient("June", "Doe", Gender.F);

        when(patientService.createPatient(any(Patient.class)))
                .thenReturn(savedPatient);

        String validPatientJson = """
                {
                  "firstName": "June",
                  "lastName": "Doe",
                  "birthDate": "1990-01-01",
                  "gender": "F",
                  "address": "10 Test Street",
                  "phone": "111-222-3333"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPatientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("June"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(patientService).createPatient(any(Patient.class));
    }

    private Patient patient(String firstName, String lastName, Gender gender) {
        return new Patient(
                firstName,
                lastName,
                LocalDate.of(1990, 1, 1),
                gender,
                "10 Test Street",
                "111-222-3333"
        );
    }
}