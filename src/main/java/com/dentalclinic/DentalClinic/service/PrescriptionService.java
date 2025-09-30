package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Prescription;
import com.dentalclinic.DentalClinic.model.User;

import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(Long id);
    List<Prescription> findAll();
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByDoctor(User doctor);
    void deleteById(Long id);
}


