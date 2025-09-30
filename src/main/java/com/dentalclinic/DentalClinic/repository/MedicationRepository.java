package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByIsActiveTrueOrderByNameAsc();
    List<Medication> findByTypeAndIsActiveTrueOrderByNameAsc(String type);
    
    @Query("SELECT m FROM Medication m WHERE m.isActive = true AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY m.name ASC")
    List<Medication> searchActiveMedications(String searchTerm);
}
