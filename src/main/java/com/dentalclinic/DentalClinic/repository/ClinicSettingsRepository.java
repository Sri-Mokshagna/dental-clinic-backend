package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.ClinicSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicSettingsRepository extends JpaRepository<ClinicSettings, Long> {
}


