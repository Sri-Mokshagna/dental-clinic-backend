package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.MedicalNote;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.MedicalNoteService;
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
@RequestMapping("/api/medical-notes")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicalNoteApiController {

    @Autowired
    private MedicalNoteService medicalNoteService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getNotesByPatient(@PathVariable Long patientId) {
        try {
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            List<MedicalNote> notes = medicalNoteService.findByPatient(patientOpt.get());
            List<Map<String, Object>> noteData = notes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(noteData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch medical notes: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Map<String, Object> body) {
        try {
            Long patientId = Long.valueOf(body.get("patientId").toString());
            Long doctorId = Long.valueOf(body.get("doctorId").toString());
            
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            
            Optional<User> doctorOpt = userService.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            
            MedicalNote note = MedicalNote.builder()
                .patient(patientOpt.get())
                .doctor(doctorOpt.get())
                .complaints((String) body.get("complaints"))
                .onExamination((String) body.get("onExamination"))
                .treatment((String) body.get("treatment"))
                .prescription((String) body.get("prescription"))
                .build();
            
            note = medicalNoteService.save(note);
            return ResponseEntity.ok(convertToResponse(note));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create medical note: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Optional<MedicalNote> noteOpt = medicalNoteService.findById(id);
            if (noteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            MedicalNote note = noteOpt.get();
            note.setComplaints((String) body.get("complaints"));
            note.setOnExamination((String) body.get("onExamination"));
            note.setTreatment((String) body.get("treatment"));
            note.setPrescription((String) body.get("prescription"));
            
            note = medicalNoteService.save(note);
            return ResponseEntity.ok(convertToResponse(note));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update medical note: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            Optional<MedicalNote> noteOpt = medicalNoteService.findById(id);
            if (noteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            medicalNoteService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Medical note deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete medical note: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(MedicalNote note) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", note.getId());
        response.put("complaints", note.getComplaints());
        response.put("onExamination", note.getOnExamination());
        response.put("treatment", note.getTreatment());
        response.put("prescription", note.getPrescription());
        response.put("date", note.getDate());
        
        if (note.getPatient() != null) {
            response.put("patient", Map.of("id", note.getPatient().getId(), "fullName", note.getPatient().getFullName()));
        }
        
        if (note.getDoctor() != null) {
            response.put("doctor", Map.of("id", note.getDoctor().getId(), "fullName", note.getDoctor().getFullName()));
            response.put("doctorName", note.getDoctor().getFullName());
        }
        
        return response;
    }
}
