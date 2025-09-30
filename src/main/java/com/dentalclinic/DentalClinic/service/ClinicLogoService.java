package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.ClinicLogo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ClinicLogoService {
    ClinicLogo save(MultipartFile file);
    Optional<ClinicLogo> getLatest();
}


