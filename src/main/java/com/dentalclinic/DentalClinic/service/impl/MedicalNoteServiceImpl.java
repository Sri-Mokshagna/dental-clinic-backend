package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.MedicalNote;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.repository.MedicalNoteRepository;
import com.dentalclinic.DentalClinic.service.MedicalNoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalNoteServiceImpl implements MedicalNoteService {

    private final MedicalNoteRepository medicalNoteRepository;

    public MedicalNoteServiceImpl(MedicalNoteRepository medicalNoteRepository) {
        this.medicalNoteRepository = medicalNoteRepository;
    }

    @Override
    public MedicalNote save(MedicalNote medicalNote) {
        return medicalNoteRepository.save(medicalNote);
    }

    @Override
    public List<MedicalNote> findByPatient(Patient patient) {
        return medicalNoteRepository.findByPatientOrderByDateDesc(patient);
    }

    @Override
    public Optional<MedicalNote> findById(Long id) {
        return medicalNoteRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        medicalNoteRepository.deleteById(id);
    }
}
