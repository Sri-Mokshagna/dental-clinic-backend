package com.dentalclinic.DentalClinic.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromWhatsApp;

    public void sendWhatsAppMessage(String to, String templateName, String... placeholders) {
        Twilio.init(accountSid, authToken);

        String body = switch (templateName) {
            case "HX230b4b964d4436ce0fb7c001bac63f6b" -> String.format(
                "Hello %s,\n\nYour %s is now available.\n\nFile: %s\nType: %s\n\nYou can access it securely through your patient portal.\nThank you,\nDental Clinic",
                placeholders[0], placeholders[1], placeholders[2], placeholders[3]
            );
            case "HX5a828301e5df1383bab110ce0d1ea4a4" -> String.format(
                "Hello %s,\n\nYour appointment update:\n\nStatus: %s\nDate & Time: %s\n\nIf you have any questions, reply to this message.\nThank you,\nDental Clinic",
                placeholders[0], placeholders[1], placeholders[2]
            );
            default -> throw new IllegalArgumentException("Invalid template name");
        };

        Message message = Message.creator(
            new PhoneNumber("whatsapp:" + to),
            new PhoneNumber(fromWhatsApp),
            body
        ).create();

        System.out.println("WhatsApp message sent: " + message.getSid());
    }
}