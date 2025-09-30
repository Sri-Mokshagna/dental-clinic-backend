package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.ClinicLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicLogoRepository extends JpaRepository<ClinicLogo, Long> {
}


