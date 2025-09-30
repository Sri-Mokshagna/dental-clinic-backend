package com.dentalclinic.DentalClinic.service;


import java.util.List;
import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Report;

public interface ReportService {
    Report save(Report report);
    List<Report> findByPatient(Patient patient);
    Optional<Report> findById(Long id);
}
