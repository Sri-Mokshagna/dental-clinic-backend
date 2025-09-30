package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Patient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileSharingService {
    
    /**
     * Upload a file for a specific patient
     */
    String uploadFileForPatient(Long patientId, MultipartFile file, String fileType, String description);
    
    /**
     * Get all files for a specific patient
     */
    List<Map<String, Object>> getPatientFiles(Long patientId);
    
    /**
     * Get file download link
     */
    String getFileDownloadLink(Long fileId);
    
    /**
     * Delete a file
     */
    boolean deleteFile(Long fileId);
    
    /**
     * Send file sharing notification via WhatsApp
     */
    void sendFileSharingNotification(Patient patient, String fileName, String fileType, String downloadLink);
}
