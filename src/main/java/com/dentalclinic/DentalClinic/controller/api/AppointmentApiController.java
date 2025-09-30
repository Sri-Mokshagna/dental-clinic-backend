package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentApiController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.findAll();
            List<Map<String, Object>> appointmentData = appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch appointments: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.findById(id);
            if (appointmentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(appointmentOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch appointment: " + e.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
        try {
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            
            List<Appointment> appointments = appointmentService.findByPatient(patientOpt.get());
            List<Map<String, Object>> appointmentData = appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch appointments: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> request) {
        try {
            Appointment appointment = new Appointment();
            
            // Parse appointment date
            String dateTimeStr = (String) request.get("appointmentDate");
            LocalDateTime appointmentDate = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            appointment.setAppointmentDate(appointmentDate);
            
            appointment.setTreatmentDetails((String) request.get("treatmentDetails"));
            appointment.setTreatmentCost(((Number) request.getOrDefault("treatmentCost", 0.0)).doubleValue());

            // Link to patient
            Long patientId = ((Number) request.get("patientId")).longValue();
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            appointment.setPatient(patientOpt.get());

            // Link to doctor
            if (request.get("doctorId") != null) {
                Long doctorId = ((Number) request.get("doctorId")).longValue();
                Optional<User> doctorOpt = userService.findById(doctorId);
                if (doctorOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
                }
                appointment.setDoctor(doctorOpt.get());
            }

            // Link to staff
            if (request.get("staffId") != null) {
                Long staffId = ((Number) request.get("staffId")).longValue();
                Optional<User> staffOpt = userService.findById(staffId);
                if (staffOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Staff not found"));
                }
                appointment.setStaff(staffOpt.get());
            }

            Appointment savedAppointment = appointmentService.save(appointment);
            return ResponseEntity.ok(convertToResponse(savedAppointment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create appointment: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    try {
        Optional<Appointment> appointmentOpt = appointmentService.findById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Appointment appointment = appointmentOpt.get();

        // Parse appointment date (optional)
        if (request.get("appointmentDate") != null) {
            try {
                String dateTimeStr = String.valueOf(request.get("appointmentDate"));
                LocalDateTime appointmentDate = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                appointment.setAppointmentDate(appointmentDate);
            } catch (Exception ignored) {}
        }

        if (request.get("treatmentDetails") != null) {
            appointment.setTreatmentDetails((String) request.get("treatmentDetails"));
        }
        if (request.get("treatmentCost") != null) {
            appointment.setTreatmentCost(((Number) request.get("treatmentCost")).doubleValue());
        }

        // Update patient
        if (request.get("patientId") != null) {
            Long patientId = ((Number) request.get("patientId")).longValue();
            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }
            appointment.setPatient(patientOpt.get());
        }

        // Update doctor
        if (request.get("doctorId") != null) {
            Long doctorId = ((Number) request.get("doctorId")).longValue();
            Optional<User> doctorOpt = userService.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            appointment.setDoctor(doctorOpt.get());
        }

        // Update staff
        if (request.get("staffId") != null) {
            Long staffId = ((Number) request.get("staffId")).longValue();
            Optional<User> staffOpt = userService.findById(staffId);
            if (staffOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Staff not found"));
            }
            appointment.setStaff(staffOpt.get());
        }

        // Update status if provided
        if (request.get("status") != null) {
            String status = String.valueOf(request.get("status")).toLowerCase();
            if (!status.equals("completed") && !status.equals("cancelled") && !status.equals("scheduled")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value"));
            }
            appointment.setStatus(status);
        }

        Appointment savedAppointment = appointmentService.save(appointment);
        System.out.println("Updating appointment with ID: " + id + " to status: " + request.get("status"));
        return ResponseEntity.ok(convertToResponse(savedAppointment));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to update appointment: " + e.getMessage()));
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete appointment: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", appointment.getId());
        response.put("appointmentDate", appointment.getAppointmentDate().toString());
        response.put("treatmentDetails", appointment.getTreatmentDetails());
        response.put("treatmentCost", appointment.getTreatmentCost());
        response.put("status", appointment.getStatus()); // ✅ Add this line
    
        if (appointment.getPatient() != null) {
            response.put("patient", Map.of(
                "id", appointment.getPatient().getId(),
                "fullName", appointment.getPatient().getFullName(),
                "phoneNumber", appointment.getPatient().getPhoneNumber()
            ));
        }
    
        if (appointment.getDoctor() != null) {
            response.put("doctor", Map.of(
                "id", appointment.getDoctor().getId(),
                "fullName", appointment.getDoctor().getFullName(),
                "username", appointment.getDoctor().getUsername()
            ));
        }
    
        if (appointment.getStaff() != null) {
            response.put("staff", Map.of(
                "id", appointment.getStaff().getId(),
                "fullName", appointment.getStaff().getFullName(),
                "username", appointment.getStaff().getUsername()
            ));
        }
    
        return response;
    }
    
}
