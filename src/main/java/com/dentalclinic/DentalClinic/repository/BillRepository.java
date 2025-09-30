package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatient(Patient patient);
   List<Bill> findByPatientOrderByIssuedAtDesc(Patient patient); 
}


