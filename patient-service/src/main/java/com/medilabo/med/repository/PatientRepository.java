package com.medilabo.med.repository;

import com.medilabo.med.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAllByLastNameIgnoreCase(String lastName);

}
