package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.service.FileSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-files")
@CrossOrigin(origins = "*")
public class FileSharingApiController {
    
    @Autowired
    private FileSharingService fileSharingService;
    
    @PostMapping("/upload/{patientId}")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            String downloadLink = fileSharingService.uploadFileForPatient(patientId, file, fileType, description);
            
            return ResponseEntity.ok(Map.of(
                "message", "File uploaded successfully",
                "downloadLink", downloadLink,
                "fileName", file.getOriginalFilename(),
                "fileSize", file.getSize()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to upload file: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientFiles(@PathVariable Long patientId) {
        try {
            List<Map<String, Object>> files = fileSharingService.getPatientFiles(patientId);
            
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
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // For now, return a simple response
            // In a real implementation, you'd serve the actual file
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file_" + fileId + "\"")
                .body(null);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            boolean deleted = fileSharingService.deleteFile(fileId);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "message", "File deleted successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "File not found"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete file: " + e.getMessage()
            ));
        }
    }
}
