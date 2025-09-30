package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.WhatsAppMessage;
import com.dentalclinic.DentalClinic.repository.WhatsAppMessageRepository;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/whatsapp")
@CrossOrigin(origins = "http://localhost:3000")
public class WhatsAppApiController {

    private final WhatsAppMessageRepository messageRepository;
    private final PatientService patientService;
    private final WhatsAppSender whatsAppSender;

    public WhatsAppApiController(WhatsAppMessageRepository messageRepository,
                                 PatientService patientService,
                                 WhatsAppSender whatsAppSender) {
        this.messageRepository = messageRepository;
        this.patientService = patientService;
        this.whatsAppSender = whatsAppSender;
    }

    // Admin broadcast
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "");
        List<Patient> patients = patientService.findAll();
        for (Patient p : patients) {
            if (p.getPhoneNumber() != null && !p.getPhoneNumber().isBlank()) {
                String name = Optional.ofNullable(p.getFullName()).orElse("Patient");
                whatsAppSender.sendMessage(p.getPhoneNumber(), message, name);
            }
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
    @PostMapping("/send-appointment-update")
public ResponseEntity<?> sendAppointmentUpdate(@RequestBody Map<String, String> body) {
    String phoneNumber = body.get("phoneNumber");
    String patientName = body.get("patientName");
    String status = body.get("status"); // scheduled, completed, cancelled
    String dateTime = body.get("dateTime");

    if (phoneNumber == null || phoneNumber.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
    }

    try {
        whatsAppSender.sendAppointmentUpdate(phoneNumber, patientName, status, dateTime);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Appointment update sent"));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to send appointment update: " + e.getMessage()));
    }
}


    // Webhook for inbound WhatsApp messages (configure this URL in Twilio)
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestParam Map<String, String> params) {
        // Common Twilio params: From, WaId, Body, ProfileName
        String from = params.getOrDefault("From", "");
        String body = params.getOrDefault("Body", "");
        String profileName = params.getOrDefault("ProfileName", "");

        WhatsAppMessage msg = WhatsAppMessage.builder()
                .fromPhone(from)
                .fromName(profileName)
                .message(body)
                .receivedAt(LocalDateTime.now())
                .build();
        messageRepository.save(msg);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/messages")
    public ResponseEntity<?> listMessages() {
        return ResponseEntity.ok(messageRepository.findAll());
    }

    // Send file to specific patient
    @PostMapping("/send-file")
    public ResponseEntity<?> sendFileToPatient(@RequestBody Map<String, String> body) {
        String phoneNumber = body.get("phoneNumber");
        String message = body.get("message");
        String fileName = body.get("fileName");
        String fileUrl = body.get("fileUrl");
        
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
        }
        
        try {
            String name = Optional.ofNullable(phoneNumber).orElse("Patient");
            String fullMessage = message + "\n\nFile: " + fileName + "\nDownload: " + fileUrl;
            whatsAppSender.sendMessage(phoneNumber, fullMessage, name);
            return ResponseEntity.ok(Map.of("status", "success", "message", "File sent to WhatsApp"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send file: " + e.getMessage()));
        }
    }

    // Send multiple files to specific patient
    @PostMapping("/send-multiple-files")
    public ResponseEntity<?> sendMultipleFilesToPatient(@RequestBody Map<String, Object> body) {
        String phoneNumber = (String) body.get("phoneNumber");
        String message = (String) body.get("message");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> files = (List<Map<String, String>>) body.get("files");
        
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
        }
        
        try {
            String name = Optional.ofNullable(phoneNumber).orElse("Patient");
            StringBuilder fullMessage = new StringBuilder(message);
            fullMessage.append("\n\nFiles:");
            
            for (Map<String, String> file : files) {
                fullMessage.append("\n• ").append(file.get("fileName"));
                fullMessage.append("\n  Download: ").append(file.get("fileUrl"));
            }
            
            whatsAppSender.sendMessage(phoneNumber, fullMessage.toString(), name);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Files sent to WhatsApp"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send files: " + e.getMessage()));
        }
    }
}


