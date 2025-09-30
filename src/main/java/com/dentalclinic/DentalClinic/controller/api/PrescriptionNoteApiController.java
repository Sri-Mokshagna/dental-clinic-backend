package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.PrescriptionNote;
import com.dentalclinic.DentalClinic.model.PrescriptionItem;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.PrescriptionNoteService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/prescription-notes")
@CrossOrigin(origins = "*")
public class PrescriptionNoteApiController {
    
    @Autowired
    private PrescriptionNoteService prescriptionNoteService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<?> getAllPrescriptionNotes() {
        try {
            List<PrescriptionNote> notes = prescriptionNoteService.findAll();
            return ResponseEntity.ok(notes.stream().map(this::toResponse).toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPrescriptionNotesByPatient(@PathVariable Long patientId) {
        try {
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            
            List<PrescriptionNote> notes = prescriptionNoteService.findByPatient(patientOpt.get());
            return ResponseEntity.ok(notes.stream().map(this::toResponse).toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getPrescriptionNotesByDoctor(@PathVariable Long doctorId) {
        try {
            Optional<User> doctorOpt = userService.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            
            List<PrescriptionNote> notes = prescriptionNoteService.findByDoctor(doctorOpt.get());
            return ResponseEntity.ok(notes.stream().map(this::toResponse).toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrescriptionNote(@PathVariable Long id) {
        try {
            Optional<PrescriptionNote> noteOpt = prescriptionNoteService.findById(id);
            if (noteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(toResponse(noteOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createPrescriptionNote(@RequestBody Map<String, Object> request) {
        try {
            Long patientId = ((Number) request.get("patientId")).longValue();
            Long doctorId = ((Number) request.get("doctorId")).longValue();
            
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            
            Optional<User> doctorOpt = userService.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            
            PrescriptionNote prescriptionNote = PrescriptionNote.builder()
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .visitDate(LocalDateTime.now())
                .complaints((String) request.get("complaints"))
                .examinationFindings((String) request.get("examinationFindings"))
                .treatmentPlan((String) request.get("treatmentPlan"))
                .prescription((String) request.get("prescription"))
                .notes((String) request.get("notes"))
                .prescriptionTotal(0.0)
                .build();
            
            // Handle prescription items
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.getOrDefault("prescriptionItems", List.of());
            List<PrescriptionItem> prescriptionItems = new ArrayList<>();
            
            for (Map<String, Object> itemData : items) {
                PrescriptionItem item = PrescriptionItem.builder()
                    .medicationName((String) itemData.get("medicationName"))
                    .dosage((String) itemData.get("dosage"))
                    .frequency((String) itemData.get("frequency"))
                    .duration((String) itemData.get("duration"))
                    .instructions((String) itemData.get("instructions"))
                    .quantity(((Number) itemData.getOrDefault("quantity", 1)).intValue())
                    .unitPrice(((Number) itemData.getOrDefault("unitPrice", 0.0)).doubleValue())
                    .build();
                
                // Calculate total price
                if (item.getUnitPrice() != null && item.getQuantity() != null) {
                    item.setTotalPrice(item.getUnitPrice() * item.getQuantity());
                }
                
                item.setPrescriptionNote(prescriptionNote);
                prescriptionItems.add(item);
            }
            
            prescriptionNote.setPrescriptionItems(prescriptionItems);
            
            PrescriptionNote saved = prescriptionNoteService.save(prescriptionNote);
            return ResponseEntity.ok(toResponse(saved));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrescriptionNote(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<PrescriptionNote> noteOpt = prescriptionNoteService.findById(id);
            if (noteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            PrescriptionNote note = noteOpt.get();
            
            if (request.get("complaints") != null) {
                note.setComplaints((String) request.get("complaints"));
            }
            if (request.get("examinationFindings") != null) {
                note.setExaminationFindings((String) request.get("examinationFindings"));
            }
            if (request.get("treatmentPlan") != null) {
                note.setTreatmentPlan((String) request.get("treatmentPlan"));
            }
            if (request.get("prescription") != null) {
                note.setPrescription((String) request.get("prescription"));
            }
            if (request.get("notes") != null) {
                note.setNotes((String) request.get("notes"));
            }
            
            PrescriptionNote saved = prescriptionNoteService.save(note);
            return ResponseEntity.ok(toResponse(saved));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrescriptionNote(@PathVariable Long id) {
        try {
            prescriptionNoteService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Prescription note deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/total")
    public ResponseEntity<?> getPrescriptionTotal(@PathVariable Long id) {
        try {
            Double total = prescriptionNoteService.calculatePrescriptionTotal(id);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private Map<String, Object> toResponse(PrescriptionNote note) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", note.getId());
        response.put("visitDate", note.getVisitDate());
        response.put("complaints", note.getComplaints());
        response.put("examinationFindings", note.getExaminationFindings());
        response.put("treatmentPlan", note.getTreatmentPlan());
        // Removed top-level prescription text and totals per requirement
        response.put("notes", note.getNotes());
        response.put("createdAt", note.getCreatedAt());
        response.put("updatedAt", note.getUpdatedAt());
        
        // Patient info
        if (note.getPatient() != null) {
            Map<String, Object> patient = new HashMap<>();
            patient.put("id", note.getPatient().getId());
            patient.put("fullName", note.getPatient().getFullName());
            patient.put("phoneNumber", note.getPatient().getPhoneNumber());
            response.put("patient", patient);
        }
        
        // Doctor info
        if (note.getDoctor() != null) {
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("id", note.getDoctor().getId());
            doctor.put("fullName", note.getDoctor().getFullName());
            doctor.put("username", note.getDoctor().getUsername());
            response.put("doctor", doctor);
        }
        
        // Prescription items
        if (note.getPrescriptionItems() != null) {
            List<Map<String, Object>> items = note.getPrescriptionItems().stream()
                .map(this::itemToResponse)
                .toList();
            response.put("prescriptionItems", items);
        }
        
        return response;
    }
    
    private Map<String, Object> itemToResponse(PrescriptionItem item) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("medicationName", item.getMedicationName());
        response.put("dosage", item.getDosage());
        response.put("frequency", item.getFrequency());
        response.put("duration", item.getDuration());
        response.put("instructions", item.getInstructions());
        response.put("quantity", item.getQuantity());
        response.put("unitPrice", item.getUnitPrice());
        response.put("totalPrice", item.getTotalPrice());
        response.put("createdAt", item.getCreatedAt());
        return response;
    }
}
