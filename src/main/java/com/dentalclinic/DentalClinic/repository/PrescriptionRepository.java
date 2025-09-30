package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Prescription;
import com.dentalclinic.DentalClinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByDoctor(User doctor);
}


