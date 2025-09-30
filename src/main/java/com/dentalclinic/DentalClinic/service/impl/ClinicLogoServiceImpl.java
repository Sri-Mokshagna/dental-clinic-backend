package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.ClinicLogo;
import com.dentalclinic.DentalClinic.repository.ClinicLogoRepository;
import com.dentalclinic.DentalClinic.service.ClinicLogoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClinicLogoServiceImpl implements ClinicLogoService {

    private final ClinicLogoRepository clinicLogoRepository;

    public ClinicLogoServiceImpl(ClinicLogoRepository clinicLogoRepository) {
        this.clinicLogoRepository = clinicLogoRepository;
    }

    @Override
    public ClinicLogo save(MultipartFile file) {
        try {
            ClinicLogo logo = ClinicLogo.builder()
                    .imageData(file.getBytes())
                    .contentType(file.getContentType() != null ? file.getContentType() : "image/png")
                    .updatedAt(LocalDateTime.now())
                    .build();
            return clinicLogoRepository.save(logo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read logo file: " + e.getMessage());
        }
    }

    @Override
    public Optional<ClinicLogo> getLatest() {
        return clinicLogoRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst();
    }
}


