package com.dentalclinic.DentalClinic.util;

import org.springframework.stereotype.Component;

@Component
public class ConsoleOTPSender {
    
    /**
     * Sends OTP to console (for development/testing purposes)
     * @param toPhoneNumber The recipient's phone number
     * @param messageBody The OTP message
     */
    public void sendOTP(String toPhoneNumber, String messageBody) {
        System.out.println("=".repeat(60));
        System.out.println("📱 OTP NOTIFICATION (Development Mode)");
        System.out.println("=".repeat(60));
        System.out.println("📞 Phone Number: " + toPhoneNumber);
        System.out.println("💬 Message: " + messageBody);
        System.out.println("=".repeat(60));
        System.out.println("⚠️  This is a development OTP. In production, this would be sent via SMS.");
        System.out.println("=".repeat(60));
    }
}
