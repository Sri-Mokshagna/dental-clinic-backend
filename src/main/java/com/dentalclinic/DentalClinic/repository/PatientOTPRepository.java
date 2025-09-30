package com.dentalclinic.DentalClinic.repository;

import com.dentalclinic.DentalClinic.model.PatientOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PatientOTPRepository extends JpaRepository<PatientOTP, Long> {
    Optional<PatientOTP> findByPhoneNumberAndOtpAndUsedFalseAndExpiresAtAfter(String phoneNumber, String otp, LocalDateTime now);
    
    @Query("SELECT p FROM PatientOTP p WHERE p.phoneNumber = :phoneNumber AND p.used = false AND p.expiresAt > :now ORDER BY p.createdAt DESC")
    Optional<PatientOTP> findLatestValidOTP(@Param("phoneNumber") String phoneNumber, @Param("now") LocalDateTime now);
    
    void deleteByPhoneNumberAndUsedTrue(String phoneNumber);
}
