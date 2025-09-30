package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.MedicalNote;
import com.dentalclinic.DentalClinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalNoteRepository extends JpaRepository<MedicalNote, Long> {
    List<MedicalNote> findByPatientOrderByDateDesc(Patient patient);
}
