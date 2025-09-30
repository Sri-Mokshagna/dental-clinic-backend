package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.PrescriptionNote;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.PrescriptionNoteRepository;
import com.dentalclinic.DentalClinic.service.PrescriptionNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionNoteServiceImpl implements PrescriptionNoteService {
    
    @Autowired
    private PrescriptionNoteRepository prescriptionNoteRepository;
    
    @Override
    public PrescriptionNote save(PrescriptionNote prescriptionNote) {
        // Calculate total before saving
        if (prescriptionNote.getPrescriptionItems() != null) {
            double total = prescriptionNote.getPrescriptionItems().stream()
                .mapToDouble(item -> item.getTotalPrice() != null ? item.getTotalPrice() : 0.0)
                .sum();
            prescriptionNote.setPrescriptionTotal(total);
        }
        
        PrescriptionNote saved = prescriptionNoteRepository.save(prescriptionNote);
        
        // Update total after saving to ensure all items are persisted
        updatePrescriptionTotal(saved.getId());
        
        return saved;
    }
    
    @Override
    public Optional<PrescriptionNote> findById(Long id) {
        return prescriptionNoteRepository.findById(id);
    }
    
    @Override
    public List<PrescriptionNote> findAll() {
        return prescriptionNoteRepository.findAll();
    }
    
    @Override
    public List<PrescriptionNote> findByPatient(Patient patient) {
        return prescriptionNoteRepository.findByPatientIdOrderByVisitDateDesc(patient.getId());
    }
    
    @Override
    public List<PrescriptionNote> findByDoctor(User doctor) {
        return prescriptionNoteRepository.findByDoctorIdOrderByVisitDateDesc(doctor.getId());
    }
    
    @Override
    public List<PrescriptionNote> findByPatientAndDoctor(Patient patient, User doctor) {
        return prescriptionNoteRepository.findByPatientIdAndDoctorIdOrderByVisitDateDesc(patient.getId(), doctor.getId());
    }
    
    @Override
    public void deleteById(Long id) {
        prescriptionNoteRepository.deleteById(id);
    }
    
    @Override
    public Double calculatePrescriptionTotal(Long prescriptionNoteId) {
        Optional<PrescriptionNote> prescriptionOpt = findById(prescriptionNoteId);
        if (prescriptionOpt.isEmpty()) {
            return 0.0;
        }
        
        PrescriptionNote prescription = prescriptionOpt.get();
        if (prescription.getPrescriptionItems() == null) {
            return 0.0;
        }
        
        return prescription.getPrescriptionItems().stream()
            .mapToDouble(item -> item.getTotalPrice() != null ? item.getTotalPrice() : 0.0)
            .sum();
    }
    
    @Override
    public void updatePrescriptionTotal(Long prescriptionNoteId) {
        Optional<PrescriptionNote> prescriptionOpt = findById(prescriptionNoteId);
        if (prescriptionOpt.isPresent()) {
            PrescriptionNote prescription = prescriptionOpt.get();
            Double total = calculatePrescriptionTotal(prescriptionNoteId);
            prescription.setPrescriptionTotal(total);
            prescriptionNoteRepository.save(prescription);
        }
    }
}
