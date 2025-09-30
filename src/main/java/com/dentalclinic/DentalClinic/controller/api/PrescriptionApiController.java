package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Medication;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Prescription;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.PrescriptionService;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000")
public class PrescriptionApiController {

    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final UserService userService;

    public PrescriptionApiController(PrescriptionService prescriptionService, PatientService patientService, UserService userService) {
        this.prescriptionService = prescriptionService;
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(prescriptionService.findAll());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(@PathVariable Long patientId) {
        return patientService.findById(patientId)
                .map(p -> ResponseEntity.ok(prescriptionService.findByPatient(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return prescriptionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req) {
        try {
            Long patientId = ((Number) req.get("patientId")).longValue();
            Long doctorId = ((Number) req.get("doctorId")).longValue();

            Optional<Patient> patientOpt = patientService.findById(patientId);
            Optional<User> doctorOpt = userService.findById(doctorId);
            if (patientOpt.isEmpty() || doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient or Doctor not found"));
            }

            Prescription p = new Prescription();
            p.setPatient(patientOpt.get());
            p.setDoctor(doctorOpt.get());
            p.setDate(LocalDate.now());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> meds = (List<Map<String, Object>>) req.getOrDefault("medications", List.of());
            List<Medication> medications = new ArrayList<>();
            for (Map<String, Object> m : meds) {
                Medication med = new Medication();
                med.setName((String) m.get("name"));
                med.setDosage((String) m.get("dosage"));
                med.setType("prescription");
                med.setDescription((String) m.get("frequency") + " - " + (String) m.get("duration"));
                medications.add(med);
            }
            p.setMedications(medications);

            p.setNotes((String) req.get("notes"));

            return ResponseEntity.ok(prescriptionService.save(p));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Optional<Prescription> opt = prescriptionService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Prescription p = opt.get();
        if (req.get("notes") != null) p.setNotes((String) req.get("notes"));
        if (req.get("date") != null) p.setDate(LocalDate.parse((String) req.get("date")));

        if (req.get("medications") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> meds = (List<Map<String, Object>>) req.get("medications");
            List<Medication> medications = new ArrayList<>();
            for (Map<String, Object> m : meds) {
                Medication med = new Medication();
                med.setName((String) m.get("name"));
                med.setDosage((String) m.get("dosage"));
                med.setType("prescription");
                med.setDescription((String) m.get("frequency") + " - " + (String) m.get("duration"));
                medications.add(med);
            }
            p.setMedications(medications);
        }
        return ResponseEntity.ok(prescriptionService.save(p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        prescriptionService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Prescription deleted"));
    }
}


