package com.dentalclinic.DentalClinic.service;

import org.springframework.web.multipart.MultipartFile;

public interface LogoService {
    
    /**
     * Upload and save clinic logo
     */
    String uploadLogo(MultipartFile file);
    
    /**
     * Get current logo URL
     */
    String getCurrentLogoUrl();
    
    /**
     * Delete current logo
     */
    boolean deleteLogo();
    
    /**
     * Check if logo exists
     */
    boolean hasLogo();
}
