package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.ClinicSettings;

import java.util.Optional;

public interface ClinicSettingsService {
    ClinicSettings save(ClinicSettings settings);
    Optional<ClinicSettings> get();
}


