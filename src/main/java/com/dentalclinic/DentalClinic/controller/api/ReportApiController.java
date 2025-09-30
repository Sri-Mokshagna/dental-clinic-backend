package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Report;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.ReportService;
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportApiController {

    private final ReportService reportService;
    private final PatientService patientService;
    private final UserService userService;
    private final WhatsAppSender whatsAppSender;

    private final Path uploadRoot;

    public ReportApiController(ReportService reportService,
                               PatientService patientService,
                               UserService userService,WhatsAppSender whatsAppSender,
                               @Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.reportService = reportService;
        this.patientService = patientService;
        this.userService = userService;
        this.whatsAppSender = whatsAppSender;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        Files.createDirectories(this.uploadRoot);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("patientId") Long patientId,
                                    @RequestParam(value = "doctorId", required = false) Long doctorId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
            }

            Optional<Patient> patientOpt = patientService.findById(patientId);
            if (patientOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Patient not found"));

            User doctor = null;
            if (doctorId != null) {
                doctor = userService.findById(doctorId).orElse(null);
            }

            String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
            String safeName = LocalDateTime.now().toString().replace(":", "-") + "-" + originalName;
            Path target = uploadRoot.resolve(safeName);
            Files.copy(file.getInputStream(), target);

            Report report = Report.builder()
                    .fileName(originalName)
                    .fileType(file.getContentType())
                    .filePath(target.toString())
                    .patient(patientOpt.get())
                    .doctor(doctor)
                    .uploadedAt(LocalDateTime.now())
                    .build();
            report = reportService.save(report);

            return ResponseEntity.ok(toResponse(report));
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to store file: " + ex.getMessage()));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> listByPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOpt = patientService.findById(patientId);
        if (patientOpt.isEmpty()) return ResponseEntity.notFound().build();
        List<Report> reports = reportService.findByPatient(patientOpt.get());
        return ResponseEntity.ok(reports.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id) throws IOException {
        Optional<Report> reportOpt = reportService.findById(id);
        if (reportOpt.isEmpty()) return ResponseEntity.notFound().build();
        Report r = reportOpt.get();
        Path path = Paths.get(r.getFilePath());
        if (!Files.exists(path)) return ResponseEntity.notFound().build();
        byte[] bytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + r.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(r.getFileType() != null ? r.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(bytes);
    }

    private Map<String, Object> toResponse(Report r) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", r.getId());
        m.put("fileName", r.getFileName());
        m.put("fileType", r.getFileType());
        m.put("filePath", r.getFilePath());
        m.put("uploadedAt", r.getUploadedAt());
        if (r.getPatient() != null) {
            m.put("patient", Map.of("id", r.getPatient().getId(), "fullName", r.getPatient().getFullName()));
        }
        if (r.getDoctor() != null) {
            m.put("doctor", Map.of("id", r.getDoctor().getId(), "fullName", r.getDoctor().getFullName()));
        }
        return m;
    }
    @PostMapping("/reports/{reportId}/send-to-whatsapp")
    public ResponseEntity<?> sendReportToWhatsApp(@PathVariable Long reportId) {
    try {
        Report report = reportService.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Patient patient = report.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Patient phone number not available"));
        }

        String fileUrl = report.getFilePath();// stored when uploading
        Map<String, String> vars = Map.of(
            "1", patient.getFullName(),
            "2", "Report",
            "3", report.getFileName(),
            "4", report.getFileType()
        );

        whatsAppSender.sendTemplateWithMedia(
            patient.getPhoneNumber(),
            "HX230b4b964d4436ce0fb7c001bac63f6b", // fileShare template SID
            vars,
            fileUrl
        );

        return ResponseEntity.ok(Map.of("status", "success", "message", "Report sent to WhatsApp"));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

}


