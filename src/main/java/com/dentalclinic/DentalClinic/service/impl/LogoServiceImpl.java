package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.service.LogoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LogoServiceImpl implements LogoService {
    
    @Value("${logo.upload.dir:uploads/logos}")
    private String uploadDir;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    public String uploadLogo(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = "clinic-logo" + fileExtension;
            
            // Delete existing logo if it exists
            deleteLogo();
            
            // Save new logo
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // Return the URL
            return baseUrl + "/api/logo/current";
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload logo: " + e.getMessage());
        }
    }
    
    @Override
    public String getCurrentLogoUrl() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return null;
            }
            
            // Look for existing logo files
            String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".svg"};
            for (String ext : extensions) {
                Path logoPath = uploadPath.resolve("clinic-logo" + ext);
                if (Files.exists(logoPath)) {
                    return baseUrl + "/api/logo/current";
                }
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public boolean deleteLogo() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return true;
            }
            
            // Delete all possible logo files
            String[] extensions = {".png", ".jpg", ".jpeg", ".gif", ".svg"};
            boolean deleted = false;
            for (String ext : extensions) {
                Path logoPath = uploadPath.resolve("clinic-logo" + ext);
                if (Files.exists(logoPath)) {
                    Files.delete(logoPath);
                    deleted = true;
                }
            }
            
            return deleted;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public boolean hasLogo() {
        return getCurrentLogoUrl() != null;
    }
}
