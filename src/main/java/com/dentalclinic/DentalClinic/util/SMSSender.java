package com.dentalclinic.DentalClinic.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component 
public class SMSSender {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    /**
     * Sends an SMS message to a given phone number.
     * @param toPhoneNumber The recipient's phone number in E.164 format (e.g., +919876543210).
     * @param messageBody The text message content.
     */
    public void sendSMS(String toPhoneNumber, String messageBody) {
        try {
            Twilio.init(accountSid, authToken);

            // Normalize phone number
            String normalizedTo = normalizePhoneNumber(toPhoneNumber);
            
            System.out.println("🚀 Attempting to send SMS...");
            System.out.println("📱 To: " + normalizedTo);
            System.out.println("📱 From: " + fromPhoneNumber);
            System.out.println("💬 Message: " + messageBody);

            // Try with configured phone number first
            if (fromPhoneNumber != null && !fromPhoneNumber.trim().isEmpty()) {
                try {
                    String normalizedFrom = normalizePhoneNumber(fromPhoneNumber);
                    
                    // Create the message
                    Message message = Message.creator(
                            new PhoneNumber(normalizedTo),
                            new PhoneNumber(normalizedFrom),
                            messageBody
                    ).create();
                    
                    System.out.println("✅ SMS sent successfully!");
                    System.out.println("📱 Message SID: " + message.getSid());
                    System.out.println("📱 Status: " + message.getStatus());
                    System.out.println("📱 To: " + normalizedTo);
                    System.out.println("📱 From: " + normalizedFrom);
                    System.out.println("💬 Message: " + messageBody);
                    return;
                    
                } catch (Exception e) {
                    System.err.println("❌ Failed with configured number: " + e.getMessage());
                    System.err.println("🔍 Error details: " + e.getClass().getSimpleName());
                    
                    // If it's a phone number issue, try to get available numbers
                    if (e.getMessage().contains("not a Twilio phone number") || 
                        e.getMessage().contains("country mismatch")) {
                        System.err.println("💡 The phone number " + fromPhoneNumber + " is not valid for SMS");
                        System.err.println("💡 Please check your Twilio console for available SMS-enabled numbers");
                        System.err.println("💡 Go to: https://console.twilio.com/us1/develop/phone-numbers/manage/incoming");
                    }
                }
            }

            // Fallback: Log the OTP for development
            System.out.println("⚠️ SMS sending failed, using console fallback");
            System.out.println("📱 OTP for " + normalizedTo + ": " + messageBody);
            System.out.println("💡 Please check your Twilio phone number configuration");

        } catch (Exception e) {
            System.err.println("❌ Critical error sending SMS: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: Log the OTP for development
            System.out.println("⚠️ Using fallback - OTP: " + messageBody);
            System.out.println("📱 For phone: " + toPhoneNumber);
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return "";
        String digits = phoneNumber.replaceAll("[^0-9+]", "");
        if (digits.startsWith("+")) return digits;
        if (digits.matches("[0-9]{10}")) return "+91" + digits; // assume India if bare 10 digits
        return "+" + digits;
    }
}
