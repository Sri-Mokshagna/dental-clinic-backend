package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.PatientOTP;
import com.dentalclinic.DentalClinic.repository.PatientOTPRepository;
import com.dentalclinic.DentalClinic.service.PatientOTPService;
import com.dentalclinic.DentalClinic.service.PatientService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PatientOTPServiceImpl implements PatientOTPService {

    private final PatientOTPRepository patientOTPRepository;
    private final PatientService patientService;

    public PatientOTPServiceImpl(PatientOTPRepository patientOTPRepository, PatientService patientService) {
        this.patientOTPRepository = patientOTPRepository;
        this.patientService = patientService;
    }

    @Override
    @Transactional
    public PatientOTP generateOTP(String phoneNumber) {
        // Clean up old OTPs for this phone number
        patientOTPRepository.deleteByPhoneNumberAndUsedTrue(phoneNumber);

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        
        // Create OTP record (expires in 10 minutes)
        LocalDateTime now = LocalDateTime.now();
        PatientOTP patientOTP = PatientOTP.builder()
            .phoneNumber(phoneNumber)
            .otp(otp)
            .createdAt(now)
            .expiresAt(now.plusMinutes(10))
            .used(false)
            .build();

        return patientOTPRepository.save(patientOTP);
    }

    @Override
    @Transactional
    public boolean verifyOTP(String phoneNumber, String otp) {
        Optional<PatientOTP> otpRecord = patientOTPRepository.findByPhoneNumberAndOtpAndUsedFalseAndExpiresAtAfter(
            phoneNumber, otp, LocalDateTime.now());
        
        if (otpRecord.isPresent()) {
            PatientOTP record = otpRecord.get();
            record.setUsed(true);
            patientOTPRepository.save(record);
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<Patient> getPatientByPhoneNumber(String phoneNumber) {
        return patientService.findByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public void cleanupExpiredOTPs() {
        patientOTPRepository.findAll().stream()
            .filter(otp -> otp.getExpiresAt().isBefore(LocalDateTime.now()))
            .forEach(otp -> patientOTPRepository.delete(otp));
    }

    @Override
    @Transactional
    public void invalidateAllOTPsForPhoneNumber(String phoneNumber) {
        patientOTPRepository.findAll().stream()
            .filter(otp -> phoneNumber.equals(otp.getPhoneNumber()) && !otp.isUsed())
            .forEach(otp -> {
                otp.setUsed(true);
                patientOTPRepository.save(otp);
            });
    }
}
