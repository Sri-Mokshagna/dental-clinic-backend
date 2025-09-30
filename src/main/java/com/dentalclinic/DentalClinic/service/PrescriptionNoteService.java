package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.PrescriptionNote;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;

import java.util.List;
import java.util.Optional;

public interface PrescriptionNoteService {
    
    PrescriptionNote save(PrescriptionNote prescriptionNote);
    
    Optional<PrescriptionNote> findById(Long id);
    
    List<PrescriptionNote> findAll();
    
    List<PrescriptionNote> findByPatient(Patient patient);
    
    List<PrescriptionNote> findByDoctor(User doctor);
    
    List<PrescriptionNote> findByPatientAndDoctor(Patient patient, User doctor);
    
    void deleteById(Long id);
    
    Double calculatePrescriptionTotal(Long prescriptionNoteId);
    
    void updatePrescriptionTotal(Long prescriptionNoteId);
}
