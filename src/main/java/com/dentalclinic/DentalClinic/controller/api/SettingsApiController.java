package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.ClinicSettings;
import com.dentalclinic.DentalClinic.service.ClinicSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:3000")
public class SettingsApiController {

    private final ClinicSettingsService settingsService;

    public SettingsApiController(ClinicSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok(settingsService.get().orElseGet(() -> ClinicSettings.builder()
                .defaultConsultationFee(1500)
                .startTime("10:00")
                .endTime("20:00")
                .slotDuration(30)
                .name("The Dental Experts")
                .address("")
                .contact("")
                .build()));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> request) {
        ClinicSettings current = settingsService.get().orElse(new ClinicSettings());

        if (request.get("defaultConsultationFee") != null) {
            current.setDefaultConsultationFee(((Number) request.get("defaultConsultationFee")).doubleValue());
        }
        if (request.get("startTime") != null) current.setStartTime((String) request.get("startTime"));
        if (request.get("endTime") != null) current.setEndTime((String) request.get("endTime"));
        if (request.get("slotDuration") != null) current.setSlotDuration(((Number) request.get("slotDuration")).intValue());
        if (request.get("name") != null) current.setName((String) request.get("name"));
        if (request.get("address") != null) current.setAddress((String) request.get("address"));
        if (request.get("contact") != null) current.setContact((String) request.get("contact"));

        ClinicSettings saved = settingsService.save(current);
        return ResponseEntity.ok(saved);
    }
}


