package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Medication;
import com.dentalclinic.DentalClinic.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicationApiController {

    @Autowired
    private MedicationService medicationService;

    @GetMapping
    public ResponseEntity<?> getAllMedications() {
        try {
            List<Medication> medications = medicationService.findActiveMedications();
            List<Map<String, Object>> medicationData = medications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(medicationData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch medications: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicationById(@PathVariable Long id) {
        try {
            Optional<Medication> medicationOpt = medicationService.findById(id);
            if (medicationOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(medicationOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch medication: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMedications(@RequestParam String q) {
        try {
            List<Medication> medications = medicationService.searchMedications(q);
            List<Map<String, Object>> medicationData = medications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(medicationData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to search medications: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getMedicationsByType(@PathVariable String type) {
        try {
            List<Medication> medications = medicationService.findByType(type);
            List<Map<String, Object>> medicationData = medications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(medicationData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch medications by type: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createMedication(@RequestBody Map<String, Object> request) {
        try {
            Medication medication = Medication.builder()
                .name((String) request.get("name"))
                .description((String) request.get("description"))
                .type((String) request.get("type"))
                .dosage((String) request.get("dosage"))
                .manufacturer((String) request.get("manufacturer"))
                .isActive((Boolean) request.getOrDefault("isActive", true))
                .build();

            Medication savedMedication = medicationService.save(medication);
            return ResponseEntity.ok(convertToResponse(savedMedication));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create medication: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Medication medication = Medication.builder()
                .name((String) request.get("name"))
                .description((String) request.get("description"))
                .type((String) request.get("type"))
                .dosage((String) request.get("dosage"))
                .manufacturer((String) request.get("manufacturer"))
                .isActive((Boolean) request.getOrDefault("isActive", true))
                .build();

            Medication updatedMedication = medicationService.update(id, medication);
            return ResponseEntity.ok(convertToResponse(updatedMedication));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update medication: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable Long id) {
        try {
            medicationService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Medication deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete medication: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(Medication medication) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", medication.getId());
        response.put("name", medication.getName());
        response.put("description", medication.getDescription());
        response.put("type", medication.getType());
        response.put("dosage", medication.getDosage());
        response.put("manufacturer", medication.getManufacturer());
        response.put("isActive", medication.isActive());
        response.put("createdAt", medication.getCreatedAt());
        response.put("updatedAt", medication.getUpdatedAt());
        return response;
    }
}
