package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Medication;
import com.dentalclinic.DentalClinic.repository.MedicationRepository;
import com.dentalclinic.DentalClinic.service.MedicationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationServiceImpl(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Override
    public Medication save(Medication medication) {
        medication.setUpdatedAt(LocalDateTime.now());
        return medicationRepository.save(medication);
    }

    @Override
    public List<Medication> findAll() {
        return medicationRepository.findAll();
    }

    @Override
    public List<Medication> findActiveMedications() {
        return medicationRepository.findByIsActiveTrueOrderByNameAsc();
    }

    @Override
    public List<Medication> findByType(String type) {
        return medicationRepository.findByTypeAndIsActiveTrueOrderByNameAsc(type);
    }

    @Override
    public List<Medication> searchMedications(String searchTerm) {
        return medicationRepository.searchActiveMedications(searchTerm);
    }

    @Override
    public Optional<Medication> findById(Long id) {
        return medicationRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        medicationRepository.deleteById(id);
    }

    @Override
    public Medication update(Long id, Medication medication) {
        Optional<Medication> existingMedication = medicationRepository.findById(id);
        if (existingMedication.isPresent()) {
            Medication med = existingMedication.get();
            med.setName(medication.getName());
            med.setDescription(medication.getDescription());
            med.setType(medication.getType());
            med.setDosage(medication.getDosage());
            med.setManufacturer(medication.getManufacturer());
            med.setActive(medication.isActive());
            med.setUpdatedAt(LocalDateTime.now());
            return medicationRepository.save(med);
        }
        throw new RuntimeException("Medication not found with id: " + id);
    }
}
