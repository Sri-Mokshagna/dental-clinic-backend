package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.PrescriptionNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionNoteRepository extends JpaRepository<PrescriptionNote, Long> {
    
    List<PrescriptionNote> findByPatientIdOrderByVisitDateDesc(Long patientId);
    
    List<PrescriptionNote> findByDoctorIdOrderByVisitDateDesc(Long doctorId);
    
    List<PrescriptionNote> findByPatientIdAndDoctorIdOrderByVisitDateDesc(Long patientId, Long doctorId);
}
