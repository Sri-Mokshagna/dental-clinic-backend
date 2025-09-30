package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.PrescriptionNote;
import com.dentalclinic.DentalClinic.service.FileSharingService;
import com.dentalclinic.DentalClinic.service.PatientOTPService;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.service.BillService;
import com.dentalclinic.DentalClinic.service.PrescriptionNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient-portal")
@CrossOrigin(origins = "*")
public class PatientPortalApiController {
    
    @Autowired
    private PatientOTPService patientOTPService;
    
    @Autowired
    private FileSharingService fileSharingService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private BillService billService;
    
    @Autowired
    private PrescriptionNoteService prescriptionNoteService;
    
    /**
     * Get patient profile with all related data
     */
    @GetMapping("/profile/{phoneNumber}")
    public ResponseEntity<?> getPatientProfile(@PathVariable String phoneNumber) {
        try {
            // Get patient by phone number
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Patient not found"
                ));
            }
            
            Patient patient = patientOpt.get();
            
            // Get patient files
            List<Map<String, Object>> files = fileSharingService.getPatientFiles(patient.getId());
            
            // Build profile response
            Map<String, Object> profile = new HashMap<>();
            profile.put("patient", Map.of(
                "id", patient.getId(),
                "fullName", patient.getFullName(),
                "phoneNumber", patient.getPhoneNumber(),
                "email", patient.getEmail(),
                "dateOfBirth", patient.getDateOfBirth(),
                "address", patient.getAddress(),
                "medicalInfo", patient.getMedicalInfo()
            ));
            
            profile.put("files", files);
            profile.put("filesCount", files.size());
            
            // TODO: Add appointments, medications, and bills
            // These would be fetched from respective services
            
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get patient profile: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get patient files
     */
    @GetMapping("/files/{phoneNumber}")
    public ResponseEntity<?> getPatientFiles(@PathVariable String phoneNumber) {
        try {
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Patient not found"
                ));
            }
            
            Patient patient = patientOpt.get();
            List<Map<String, Object>> files = fileSharingService.getPatientFiles(patient.getId());
            
            return ResponseEntity.ok(Map.of(
                "files", files,
                "count", files.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get files: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get patient appointments
     */
    @GetMapping("/appointments/{phoneNumber}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable String phoneNumber) {
        try {
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Patient not found"
                ));
            }
            
            Patient patient = patientOpt.get();
            List<Appointment> appointments = appointmentService.findByPatient(patient);
            
            List<Map<String, Object>> appointmentList = appointments.stream()
                .map(this::appointmentToResponse)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "appointments", appointmentList,
                "count", appointmentList.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get appointments: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get patient prescriptions
     */
    @GetMapping("/prescriptions/{phoneNumber}")
    public ResponseEntity<?> getPatientPrescriptions(@PathVariable String phoneNumber) {
        try {
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Patient not found"
                ));
            }
            
            Patient patient = patientOpt.get();
            List<PrescriptionNote> prescriptions = prescriptionNoteService.findByPatient(patient);
            
            List<Map<String, Object>> prescriptionList = prescriptions.stream()
                .map(this::prescriptionToResponse)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "prescriptions", prescriptionList,
                "count", prescriptionList.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get prescriptions: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get patient bills
     */
    @GetMapping("/bills/{phoneNumber}")
    public ResponseEntity<?> getPatientBills(@PathVariable String phoneNumber) {
        try {
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Patient not found"
                ));
            }
            
            Patient patient = patientOpt.get();
            List<Bill> bills = billService.findByPatient(patient);
            
            List<Map<String, Object>> billList = bills.stream()
                .map(this::billToResponse)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "bills", billList,
                "count", billList.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get bills: " + e.getMessage()
            ));
        }
    }
    
    // Helper methods for response formatting
    private Map<String, Object> appointmentToResponse(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", appointment.getId());
        response.put("appointmentDate", appointment.getAppointmentDate());
        response.put("status", appointment.getStatus());
        response.put("treatmentDetails", appointment.getTreatmentDetails());
        
        if (appointment.getDoctor() != null) {
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("id", appointment.getDoctor().getId());
            doctor.put("fullName", appointment.getDoctor().getFullName());
            response.put("doctor", doctor);
        }
        
        return response;
    }
    
    private Map<String, Object> prescriptionToResponse(PrescriptionNote prescription) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", prescription.getId());
        response.put("visitDate", prescription.getVisitDate());
        response.put("complaints", prescription.getComplaints());
        response.put("treatmentPlan", prescription.getTreatmentPlan());
        // Removed top-level prescription text and totals; keep items only
        response.put("notes", prescription.getNotes());
        
        if (prescription.getDoctor() != null) {
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("id", prescription.getDoctor().getId());
            doctor.put("fullName", prescription.getDoctor().getFullName());
            response.put("doctor", doctor);
        }
        
        if (prescription.getPrescriptionItems() != null) {
            List<Map<String, Object>> items = prescription.getPrescriptionItems().stream()
                .map(this::itemToResponse)
                .toList();
            response.put("prescriptionItems", items);
        }
        
        return response;
    }
    
    private Map<String, Object> itemToResponse(com.dentalclinic.DentalClinic.model.PrescriptionItem item) {
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
        return response;
    }
    
    private Map<String, Object> billToResponse(Bill bill) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", bill.getId());
        response.put("amount", bill.getAmount());
        response.put("status", bill.getStatus());
        response.put("issuedAt", bill.getIssuedAt());
        response.put("paymentDate", bill.getPaymentDate());
        response.put("paymentMethod", bill.getPaymentMethod());
        
        if (bill.getItems() != null) {
            List<Map<String, Object>> items = bill.getItems().stream()
                .map(item -> {
                    Map<String, Object> itemResponse = new HashMap<>();
                    itemResponse.put("description", item.getDescription());
                    itemResponse.put("cost", item.getCost());
                    return itemResponse;
                })
                .toList();
            response.put("items", items);
        }
        
        return response;
    }
}
