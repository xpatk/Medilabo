package com.medilabo.med.service;

import com.medilabo.med.exception.PatientNotFoundException;
import com.medilabo.med.model.Gender;
import com.medilabo.med.model.Patient;
import com.medilabo.med.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void returnsAllPatientsFromRepository() {
        List<Patient> patients = List.of(
                patient("June", "Doe", Gender.F),
                patient("Alain", "Tester", Gender.M)
        );

        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).containsExactlyElementsOf(patients);
        verify(patientRepository).findAll();
     }

     @Test
    void returnsPatientById() {
         Patient patient = patient("Ada", "Lovelace", Gender.F);

         when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

         Patient result = patientService.getPatientById(1L);

         assertThat(result).isSameAs(patient);
         verify(patientRepository).findById(1L);
     }

     @Test
    void throwsPatientNotFoundExceptionWhenPatientDoesNotExists() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

         assertThatThrownBy(() -> patientService.getPatientById(99L))
                 .isInstanceOf(PatientNotFoundException.class);

         verify(patientRepository).findById(99L);
     }
    @Test
    void returnsPatientsByLastNameIgnoringCase() {
        List<Patient> patients = List.of(
                patient("June", "Doe", Gender.F)
        );

        when(patientRepository.findAllByLastNameIgnoreCase("doe"))
                .thenReturn(patients);

        List<Patient> result = patientService.getPatientsByLastName("doe");

        assertThat(result).containsExactlyElementsOf(patients);
        verify(patientRepository).findAllByLastNameIgnoreCase("doe");
    }

    @Test
    void savesPatientWhenCreating() {
        Patient patient = patient("June", "Doe", Gender.F);

        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.createPatient(patient);

        assertThat(result).isSameAs(patient);
        verify(patientRepository).save(patient);
    }

    @Test
    void updatesAllPatientFields() {
        Patient existingPatient = patient("June", "OldName", Gender.F);
        Patient updatedPatient = new Patient(
                "Alain",
                "NewName",
                LocalDate.of(1985, 6, 15),
                Gender.M,
                "25 New Street",
                "999-888-7777"
        );

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(existingPatient))
                .thenReturn(existingPatient);

        Patient result = patientService.updatePatient(1L, updatedPatient);

        assertThat(result).isSameAs(existingPatient);
        assertThat(result.getFirstName()).isEqualTo("Alain");
        assertThat(result.getLastName()).isEqualTo("NewName");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1985, 6, 15));
        assertThat(result.getGender()).isEqualTo(Gender.M);
        assertThat(result.getAddress()).isEqualTo("25 New Street");
        assertThat(result.getPhone()).isEqualTo("999-888-7777");

        verify(patientRepository).findById(1L);
        verify(patientRepository).save(existingPatient);
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
