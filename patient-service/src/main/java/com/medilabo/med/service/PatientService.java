package com.medilabo.med.service;

import com.medilabo.med.model.Patient;
import com.medilabo.med.repository.PatientRepository;
import org.springframework.stereotype.Service;
import com.medilabo.med.exception.PatientNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gère les opérations métier liées aux dossiers patients.
 *
 * <p>Ce service centralise la consultation, la recherche, la création
 * et la mise à jour des informations personnelles des patients.</p>
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retourne l'ensemble des patients enregistrés.
     *
     * @return la liste des patients
     */
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Recherche les patients dont le nom correspond à la valeur fournie,
     * sans tenir compte des majuscules et des minuscules.
     *
     * @param lastName nom de famille recherché
     * @return les patients correspondant au nom recherché
     */
    @Transactional(readOnly = true)
    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepository.findAllByLastNameIgnoreCase(lastName);
    }

    /**
     * Recherche un patient à partir de son identifiant.
     *
     * @param id identifiant du patient
     * @return le patient correspondant
     * @throws PatientNotFoundException si aucun patient ne correspond à cet identifiant
     */
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }

    /**
     * Crée et enregistre un nouveau dossier patient.
     *
     * @param patient informations validées du patient à créer
     * @return le patient enregistré, avec son identifiant généré
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Met à jour les informations personnelles d'un patient existant.
     *
     * @param id identifiant du patient à modifier
     * @param updatedPatient nouvelles informations validées
     * @return le dossier patient mis à jour
     * @throws PatientNotFoundException si le patient n'existe pas
     */
    @Transactional
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient existingPatient = getPatientById(id);

        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setBirthDate(updatedPatient.getBirthDate());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setAddress(updatedPatient.getAddress());
        existingPatient.setPhone(updatedPatient.getPhone());

        return patientRepository.save(existingPatient);
    }
}