package com.dentalclinic.DentalClinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
 
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // ✅ Find patients by doctor entity
    List<Patient> findByDoctor(User doctor);
    Optional<Patient> findById(Long id);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByFullNameAndPhoneNumber(String fullName, String phoneNumber);
    
}
