package com.medilabo.med.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super ("Patient was not found.");
    }
}

