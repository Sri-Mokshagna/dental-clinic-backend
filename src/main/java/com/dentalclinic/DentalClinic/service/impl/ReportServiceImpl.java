package com.dentalclinic.DentalClinic.service.impl;


import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Report;
import com.dentalclinic.DentalClinic.repository.ReportRepository;
import com.dentalclinic.DentalClinic.service.ReportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report save(Report report) {
        report.setUploadedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @Override
    public List<Report> findByPatient(Patient patient) {
        return reportRepository.findByPatient(patient);
    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }
}
