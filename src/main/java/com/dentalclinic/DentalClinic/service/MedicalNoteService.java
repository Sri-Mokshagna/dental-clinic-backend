package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.MedicalNote;
import com.dentalclinic.DentalClinic.model.Patient;
import java.util.List;
import java.util.Optional;

public interface MedicalNoteService {
    MedicalNote save(MedicalNote medicalNote);
    List<MedicalNote> findByPatient(Patient patient);
    Optional<MedicalNote> findById(Long id);
    void deleteById(Long id);
}
