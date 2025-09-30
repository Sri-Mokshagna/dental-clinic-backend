package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.PatientOTP;
import java.util.Optional;

public interface PatientOTPService {
    PatientOTP generateOTP(String phoneNumber);
    boolean verifyOTP(String phoneNumber, String otp);
    Optional<Patient> getPatientByPhoneNumber(String phoneNumber);
    void cleanupExpiredOTPs();
    
    void invalidateAllOTPsForPhoneNumber(String phoneNumber);
}
