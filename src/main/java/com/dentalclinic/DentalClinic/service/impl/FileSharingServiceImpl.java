package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.PatientFile;
import com.dentalclinic.DentalClinic.repository.PatientFileRepository;
import com.dentalclinic.DentalClinic.repository.PatientRepository;
import com.dentalclinic.DentalClinic.service.FileSharingService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;
import com.dentalclinic.DentalClinic.util.WhatsAppTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileSharingServiceImpl implements FileSharingService {
    
    @Autowired
    private PatientFileRepository patientFileRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private WhatsAppSender whatsAppSender;
    
    @Autowired
    private WhatsAppTemplates whatsAppTemplates;

    @Value("${twilio.whatsapp.template.fileShare}")
    private String fileShareTemplateSid;

    
    @Value("${file.upload.dir:uploads/patient-files}")
    private String uploadDir;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    public String uploadFileForPatient(Long patientId, MultipartFile file, String fileType, String description) {
        try {
            // Get patient
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (patientOpt.isEmpty()) {
                throw new RuntimeException("Patient not found");
            }
            
            Patient patient = patientOpt.get();
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Save file record to database
            PatientFile patientFile = new PatientFile();
            patientFile.setPatient(patient);
            patientFile.setOriginalFileName(originalFilename);
            patientFile.setStoredFileName(uniqueFilename);
            patientFile.setFileType(fileType);
            patientFile.setDescription(description);
            patientFile.setFileSize(file.getSize());
            patientFile.setUploadDate(LocalDateTime.now());
            patientFile.setFilePath(filePath.toString());
            
            PatientFile savedFile = patientFileRepository.save(patientFile);
            
            // Generate download link
            String downloadLink = baseUrl + "/api/patient-files/download/" + savedFile.getId();
            
            // Send WhatsApp notification
            sendFileSharingNotification(patient, originalFilename, fileType, downloadLink);
            
            return downloadLink;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, Object>> getPatientFiles(Long patientId) {
        List<PatientFile> files = patientFileRepository.findByPatientId(patientId);
        List<Map<String, Object>> fileList = new ArrayList<>();
        
        for (PatientFile file : files) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", file.getId());
            fileInfo.put("originalFileName", file.getOriginalFileName());
            fileInfo.put("fileType", file.getFileType());
            fileInfo.put("description", file.getDescription());
            fileInfo.put("fileSize", file.getFileSize());
            fileInfo.put("uploadDate", file.getUploadDate());
            fileInfo.put("downloadLink", baseUrl + "/api/patient-files/download/" + file.getId());
            fileList.add(fileInfo);
        }
        
        return fileList;
    }
    
    @Override
    public String getFileDownloadLink(Long fileId) {
        Optional<PatientFile> fileOpt = patientFileRepository.findById(fileId);
        if (fileOpt.isEmpty()) {
            throw new RuntimeException("File not found");
        }
        
        return baseUrl + "/api/patient-files/download/" + fileId;
    }
    
    @Override
    public boolean deleteFile(Long fileId) {
        try {
            Optional<PatientFile> fileOpt = patientFileRepository.findById(fileId);
            if (fileOpt.isEmpty()) {
                return false;
            }
            
            PatientFile file = fileOpt.get();
            
            // Delete physical file
            Path filePath = Paths.get(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // Delete database record
            patientFileRepository.delete(file);
            
            return true;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }
    
    @Override
public void sendFileSharingNotification(Patient patient, String fileName, String fileType, String downloadLink) {
    try {
        // Prepare variables as required by the template:
        Map<String, String> vars = new HashMap<>();
        vars.put("1", patient.getFullName() != null ? patient.getFullName() : "Patient");
        // feature ({{2}}) - the type of feature, e.g., "bill" / "prescription"
        vars.put("2", fileType != null ? fileType : "document");
        vars.put("3", fileName != null ? fileName : "file.pdf");
        vars.put("4", fileType != null ? fileType : "file");

        // downloadLink must be HTTPS and publicly accessible for Twilio to fetch as media.
        // We attach as media; template body fields still contain names / type.
        whatsAppSender.sendTemplateWithMedia(patient.getPhoneNumber(), fileShareTemplateSid, vars, downloadLink);
    } catch (Exception e) {
        System.err.println("Failed to send file sharing notification: " + e.getMessage());
    }
}

}
