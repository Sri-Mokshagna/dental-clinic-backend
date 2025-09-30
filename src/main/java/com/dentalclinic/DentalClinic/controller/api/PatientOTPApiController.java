package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.PatientOTP;
import com.dentalclinic.DentalClinic.service.PatientOTPService;
import com.dentalclinic.DentalClinic.util.SMSSender;
import com.dentalclinic.DentalClinic.util.ConsoleOTPSender;
import com.dentalclinic.DentalClinic.util.TwilioPhoneNumberChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient-otp")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientOTPApiController {

    @Autowired
    private PatientOTPService patientOTPService;

    @Autowired
    private SMSSender smsSender;
    
    @Autowired
    private ConsoleOTPSender consoleOTPSender;
    
    @Autowired
    private TwilioPhoneNumberChecker phoneNumberChecker;

    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
            }

            // Check if patient exists with this phone number
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No patient found with this phone number"));
            }

            // Generate OTP
            PatientOTP otpRecord = patientOTPService.generateOTP(phoneNumber);

            // Send OTP via SMS with fallback
            String message = String.format("Your OTP for clinic portal access is: %s. This OTP is valid for 10 minutes.", otpRecord.getOtp());
            
            try {
                smsSender.sendSMS(phoneNumber, message);
            } catch (Exception e) {
                System.err.println("❌ SMS failed, using console fallback: " + e.getMessage());
                consoleOTPSender.sendOTP(phoneNumber, message);
            }

            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "phoneNumber", phoneNumber,
                "expiresIn", 10 // minutes
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String otp = request.get("otp");

            if (phoneNumber == null || otp == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number and OTP are required"));
            }

            // Verify OTP
            boolean isValid = patientOTPService.verifyOTP(phoneNumber, otp);
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }

            // Get patient details
            Optional<Patient> patientOpt = patientOTPService.getPatientByPhoneNumber(phoneNumber);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));
            }

            Patient patient = patientOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("patient", Map.of(
                "id", patient.getId(),
                "fullName", patient.getFullName(),
                "phoneNumber", patient.getPhoneNumber(),
                "email", patient.getEmail(),
                "age", patient.getAge(),
                "gender", patient.getGender(),
                "address", patient.getAddress(),
                "medicalInfo", patient.getMedicalInfo()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to verify OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
            }

            // Mark all OTPs for this phone number as used (effectively logging out)
            patientOTPService.invalidateAllOTPsForPhoneNumber(phoneNumber);
            
            return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "phoneNumber", phoneNumber
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to logout: " + e.getMessage()));
        }
    }

    @GetMapping("/check-phone-numbers")
    public ResponseEntity<?> checkPhoneNumbers() {
        try {
            phoneNumberChecker.checkPhoneNumbers();
            return ResponseEntity.ok(Map.of(
                "message", "Phone numbers checked successfully. Check console for details.",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to check phone numbers: " + e.getMessage()
            ));
        }
    }
}
