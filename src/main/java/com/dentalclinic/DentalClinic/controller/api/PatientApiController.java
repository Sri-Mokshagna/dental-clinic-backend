package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        try {
            List<Patient> patients = patientService.findAll();
            List<Map<String, Object>> patientData = patients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(patientData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch patients: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        try {
            Optional<Patient> patientOpt = patientService.findById(id);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(patientOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch patient: " + e.getMessage()));
        }
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getPatientByPhone(@PathVariable String phoneNumber) {
        try {
            Optional<Patient> patientOpt = patientService.findByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(patientOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch patient: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Map<String, Object> request) {
        try {
            Patient patient = new Patient();
            patient.setFullName((String) request.get("fullName"));
            if (request.get("age") != null) patient.setAge(((Number) request.get("age")).intValue());
            patient.setGender((String) request.get("gender"));
            patient.setPhoneNumber((String) request.get("phoneNumber"));
            patient.setAddress((String) request.get("address"));
            patient.setEmail((String) request.get("email"));
            patient.setMedicalInfo((String) request.get("medicalInfo"));
            patient.setTreatmentAmount(((Number) request.getOrDefault("treatmentAmount", 0.0)).doubleValue());

            // Link to doctor if provided
            if (request.get("doctorId") != null) {
                Long doctorId = ((Number) request.get("doctorId")).longValue();
                Optional<User> doctorOpt = userService.findById(doctorId);
                doctorOpt.ifPresent(patient::setDoctor);
            }

            // Created by (registrar)
            if (request.get("createdById") != null) {
                Long createdById = ((Number) request.get("createdById")).longValue();
                userService.findById(createdById).ifPresent(patient::setCreatedBy);
            }

            Patient savedPatient = patientService.save(patient);
            return ResponseEntity.ok(convertToResponse(savedPatient));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create patient: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Patient> patientOpt = patientService.findById(id);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Patient patient = patientOpt.get();
            if (request.get("fullName") != null) patient.setFullName((String) request.get("fullName"));
            if (request.get("age") != null) patient.setAge(((Number) request.get("age")).intValue());
            if (request.get("gender") != null) patient.setGender((String) request.get("gender"));
            if (request.get("phoneNumber") != null) patient.setPhoneNumber((String) request.get("phoneNumber"));
            if (request.get("address") != null) patient.setAddress((String) request.get("address"));
            if (request.get("email") != null) patient.setEmail((String) request.get("email"));
            if (request.get("medicalInfo") != null) patient.setMedicalInfo((String) request.get("medicalInfo"));
            if (request.get("treatmentAmount") != null) patient.setTreatmentAmount(((Number) request.get("treatmentAmount")).doubleValue());

            // Update doctor if provided
            if (request.get("doctorId") != null) {
                Long doctorId = ((Number) request.get("doctorId")).longValue();
                Optional<User> doctorOpt = userService.findById(doctorId);
                doctorOpt.ifPresent(patient::setDoctor);
            }

            Patient savedPatient = patientService.save(patient);
            return ResponseEntity.ok(convertToResponse(savedPatient));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update patient: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        try {
            patientService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Patient deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete patient: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(Patient patient) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", patient.getId());
        response.put("fullName", patient.getFullName());
        response.put("age", patient.getAge());
        response.put("gender", patient.getGender());
        response.put("phoneNumber", patient.getPhoneNumber());
        response.put("address", patient.getAddress());
        response.put("email", patient.getEmail());
        response.put("medicalInfo", patient.getMedicalInfo());
        response.put("treatmentAmount", patient.getTreatmentAmount());
        
        if (patient.getDoctor() != null) {
            response.put("doctor", Map.of(
                "id", patient.getDoctor().getId(),
                "fullName", patient.getDoctor().getFullName(),
                "username", patient.getDoctor().getUsername()
            ));
        }
        if (patient.getCreatedBy() != null) {
            response.put("createdBy", Map.of(
                "id", patient.getCreatedBy().getId(),
                "username", patient.getCreatedBy().getUsername(),
                "fullName", patient.getCreatedBy().getFullName()
            ));
        }
        // user link removed
        
        return response;
    }
}
