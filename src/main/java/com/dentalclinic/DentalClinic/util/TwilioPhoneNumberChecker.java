package com.dentalclinic.DentalClinic.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.base.ResourceSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TwilioPhoneNumberChecker {
    
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;
    
    /**
     * Lists all available phone numbers and their capabilities
     */
    public void checkPhoneNumbers() {
        try {
            Twilio.init(accountSid, authToken);
            
            System.out.println("🔍 Checking Twilio phone numbers...");
            System.out.println("=".repeat(60));
            
            ResourceSet<IncomingPhoneNumber> rs = IncomingPhoneNumber.reader().read();
            java.util.List<IncomingPhoneNumber> phoneNumbers = new java.util.ArrayList<>();
            for (IncomingPhoneNumber number : rs) {
                phoneNumbers.add(number);
            }
            
            if (phoneNumbers.isEmpty()) {
                System.out.println("❌ No phone numbers found in your Twilio account");
                System.out.println("💡 Please purchase a phone number from: https://console.twilio.com/us1/develop/phone-numbers/manage/incoming");
                return;
            }
            
            System.out.println("📱 Found " + phoneNumbers.size() + " phone number(s):");
            System.out.println("=".repeat(60));
            
            for (IncomingPhoneNumber number : phoneNumbers) {
                System.out.println("📞 Phone Number: " + number.getPhoneNumber());
                System.out.println("   Friendly Name: " + number.getFriendlyName());
                System.out.println("   SMS Capable: " + (number.getCapabilities().getSms() ? "✅ Yes" : "❌ No"));
                System.out.println("   Voice Capable: " + (number.getCapabilities().getVoice() ? "✅ Yes" : "❌ No"));
                System.out.println("   MMS Capable: " + (number.getCapabilities().getMms() ? "✅ Yes" : "❌ No"));
                System.out.println("   Status: " + number.getStatus());
                System.out.println("   SID: " + number.getSid());
                System.out.println("-".repeat(40));
            }
            
            // Find SMS-capable numbers
            List<IncomingPhoneNumber> smsCapableNumbers = phoneNumbers.stream()
                .filter(number -> number.getCapabilities().getSms())
                .toList();
                
            if (smsCapableNumbers.isEmpty()) {
                System.out.println("❌ No SMS-capable phone numbers found!");
                System.out.println("💡 Please purchase a phone number with SMS capability");
            } else {
                System.out.println("✅ SMS-capable numbers found:");
                for (IncomingPhoneNumber number : smsCapableNumbers) {
                    System.out.println("   📱 " + number.getPhoneNumber() + " (SID: " + number.getSid() + ")");
                }
                System.out.println("💡 Update your application.properties with one of these numbers");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error checking phone numbers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
