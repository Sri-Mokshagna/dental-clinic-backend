package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.ClinicSettings;
import com.dentalclinic.DentalClinic.repository.ClinicSettingsRepository;
import com.dentalclinic.DentalClinic.service.ClinicSettingsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClinicSettingsServiceImpl implements ClinicSettingsService {

    private final ClinicSettingsRepository repository;

    public ClinicSettingsServiceImpl(ClinicSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClinicSettings save(ClinicSettings settings) {
        return repository.save(settings);
    }

    @Override
    public Optional<ClinicSettings> get() {
        return repository.findAll().stream().findFirst();
    }
}


