package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.ClinicLogo;
import com.dentalclinic.DentalClinic.service.ClinicLogoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/logo")
@CrossOrigin(origins = "http://localhost:3000")
public class LogoApiController {

    private final ClinicLogoService clinicLogoService;

    public LogoApiController(ClinicLogoService clinicLogoService) {
        this.clinicLogoService = clinicLogoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            ClinicLogo saved = clinicLogoService.save(file);
            // Cache-busting URL via timestamp
            String url = "/api/logo/image?ts=" + saved.getUpdatedAt().toString();
            return ResponseEntity.ok(Map.of("logoUrl", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> image() {
        Optional<ClinicLogo> logoOpt = clinicLogoService.getLatest();
        if (logoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ClinicLogo logo = logoOpt.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.parseMediaType(logo.getContentType()))
                .body(logo.getImageData());
    }

    @GetMapping("/url")
    public ResponseEntity<?> url() {
        Optional<ClinicLogo> logoOpt = clinicLogoService.getLatest();
        if (logoOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("hasLogo", false));
        }
        ClinicLogo logo = logoOpt.get();
        return ResponseEntity.ok(Map.of(
                "hasLogo", true,
                "logoUrl", "/api/logo/image?ts=" + logo.getUpdatedAt().toString()
        ));
    }
}
