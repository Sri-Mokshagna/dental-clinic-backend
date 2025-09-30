package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.Patient;

import java.util.List;
import java.util.Optional;

public interface BillService {
    Bill save(Bill bill);
    Optional<Bill> findById(Long id);
    List<Bill> findAll();
    List<Bill> findByPatient(Patient patient);
    void deleteById(Long id);
}


