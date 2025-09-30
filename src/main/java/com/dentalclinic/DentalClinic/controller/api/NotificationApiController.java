package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.PatientFile;
import com.dentalclinic.DentalClinic.repository.PatientFileRepository;
import com.dentalclinic.DentalClinic.service.FileSharingService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.util.PdfGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/notify")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationApiController {

    private final PatientService patientService;
    private final PatientFileRepository patientFileRepository;
    private final FileSharingService fileSharingService;

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${application.base-url:http://localhost:8080}")
    private String baseUrl;

    public NotificationApiController(PatientService patientService,
                                     PatientFileRepository patientFileRepository,
                                     FileSharingService fileSharingService) {
        this.patientService = patientService;
        this.patientFileRepository = patientFileRepository;
        this.fileSharingService = fileSharingService;
    }

    /**
     * Create a small PDF and send it using the file-sharing template.
     * JSON body:
     * {
     *   "patientId": 123,
     *   "title": "Bill #123",
     *   "fileType": "Bill",
     *   "content": "Short description or HTML-free text."
     * }
     */
    @PostMapping("/send-file")
    public ResponseEntity<?> sendGeneratedFile(@RequestBody Map<String, Object> body) {
        try {
            Long patientId = body.get("patientId") == null ? null : ((Number) body.get("patientId")).longValue();
            if (patientId == null) return ResponseEntity.badRequest().body(Map.of("error", "patientId required"));

            Optional<Patient> pOpt = patientService.findById(patientId);
            if (pOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "patient not found"));
            Patient patient = pOpt.get();

            String title = (String) body.getOrDefault("title", "Document");
            String fileType = (String) body.getOrDefault("fileType", "Document");
            String content = (String) body.getOrDefault("content", title);

            // create PDF file in uploadDir
            String uniqueName = UUID.randomUUID().toString() + ".pdf";
            Path outDir = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(outDir);
            Path outPath = outDir.resolve(uniqueName);

            PdfGeneratorUtil.generateSimplePdf(title, content, outPath);

            // create PatientFile record
            PatientFile pf = new PatientFile();
            pf.setPatient(patient);
            pf.setOriginalFileName(title + ".pdf");
            pf.setStoredFileName(uniqueName);
            pf.setFileType(fileType);
            pf.setDescription("Auto-generated via notification API");
            pf.setFileSize(Files.size(outPath));
            pf.setUploadDate(LocalDateTime.now());

            PatientFile saved = patientFileRepository.save(pf);

            String downloadLink = baseUrl + "/api/patient-files/download/" + saved.getId();

            // send notification using existing service
            fileSharingService.sendFileSharingNotification(patient, saved.getOriginalFileName(), fileType, downloadLink);

            return ResponseEntity.ok(Map.of("message", "sent", "downloadLink", downloadLink));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed: " + e.getMessage()));
        }
    }
}
