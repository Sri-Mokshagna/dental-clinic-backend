package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Prescription;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.PrescriptionRepository;
import com.dentalclinic.DentalClinic.service.PrescriptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Override
    public Prescription save(Prescription prescription) {
        if (prescription.getDate() == null) {
            prescription.setDate(LocalDate.now());
        }
        return prescriptionRepository.save(prescription);
    }

    @Override
    public Optional<Prescription> findById(Long id) {
        return prescriptionRepository.findById(id);
    }

    @Override
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }

    @Override
    public List<Prescription> findByPatient(Patient patient) {
        return prescriptionRepository.findByPatient(patient);
    }

    @Override
    public List<Prescription> findByDoctor(User doctor) {
        return prescriptionRepository.findByDoctor(doctor);
    }

    @Override
    public void deleteById(Long id) {
        prescriptionRepository.deleteById(id);
    }
}


