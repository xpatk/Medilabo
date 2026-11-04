package com.medilabo.med.controller;

import com.medilabo.med.model.Patient;
import com.medilabo.med.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expose les endpoints REST de gestion des dossiers patients.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Retourne la liste de tous les patients.
     *
     * @return liste des patients enregistrés, éventuellement vide
     */
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    /**
     * Retourne les informations personnelles d'un patient.
     *
     * @param id identifiant du patient
     * @return patient correspondant à l'identifiant
     */
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    /**
     * Recherche les patients par nom exact, sans distinction de casse.
     *
     * @param lastName nom de famille recherché
     * @return liste des patients correspondants, éventuellement vide
     */
    @GetMapping("/search")
    public List<Patient> getPatientsByLastName(
            @RequestParam String lastName) {
        return patientService.getPatientsByLastName(lastName);
    }

    /**
     * Crée un nouveau dossier patient.
     *
     * @param patient informations validées du patient
     * @return patient enregistré avec son identifiant généré
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    /**
     * Met à jour les informations personnelles d'un patient.
     *
     * @param id identifiant du patient à modifier
     * @param patient nouvelles informations validées
     * @return dossier patient mis à jour
     */
    @PutMapping("/{id}")
    public Patient updatePatient(
            @PathVariable Long id,
            @Valid
            @RequestBody Patient patient
    ) {
        return patientService.updatePatient(id, patient);
    }
}
