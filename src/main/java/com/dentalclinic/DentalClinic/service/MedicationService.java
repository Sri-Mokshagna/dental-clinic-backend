package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Medication;
import java.util.List;
import java.util.Optional;

public interface MedicationService {
    Medication save(Medication medication);
    List<Medication> findAll();
    List<Medication> findActiveMedications();
    List<Medication> findByType(String type);
    List<Medication> searchMedications(String searchTerm);
    Optional<Medication> findById(Long id);
    void deleteById(Long id);
    Medication update(Long id, Medication medication);
}
