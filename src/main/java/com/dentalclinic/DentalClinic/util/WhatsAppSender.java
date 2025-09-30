package com.dentalclinic.DentalClinic.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component 
public class WhatsAppSender {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromWhatsApp;

    @Value("${twilio.whatsapp.template.appointmentUpdate}")
    private String appointmentUpdateTemplateSid;
    // Optional: Twilio Content Template SID (for templated WhatsApp messages)
   private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a WhatsApp message to a given phone number.
     * If a Content SID is configured, sends using the pre-approved template and variables.
     * @param toPhoneNumber The recipient's phone number in E.164 format (e.g., +919876543210).
     * @param messageBody The text message content.
     * @param nameString The recipient name to inject into the template (variable 1).
     */
    public void sendMessage(String toPhoneNumber, String messageBody, String nameString) {
        // try {
        //     Twilio.init(accountSid, authToken);

        //     // Ensure fromWhatsApp value includes the "whatsapp:" prefix
        //     String from = fromWhatsApp.startsWith("whatsapp:") ? fromWhatsApp : ("whatsapp:" + fromWhatsApp);

        //     Message message;
        //         // Fallback to plain text message
        //         message = Message.creator(
        //                 new PhoneNumber(normalizeToWhatsApp(toPhoneNumber)),
        //                 new PhoneNumber(from),
        //                 messageBody
        //         ).create();

        //     System.out.println("✅ WhatsApp message sent! SID: " + message.getSid());

        // } catch (Exception e) {
        //     System.err.println("❌ Error sending WhatsApp message: " + e.getMessage());
        // }
        try {
            Twilio.init(accountSid, authToken);
            String variablesJson = String.format("{\"1\":\"%s\", \"2\":\"%s\"}", nameString,messageBody);
            Message message = Message.creator(
                    new PhoneNumber(normalizeToWhatsApp(toPhoneNumber)),
                    new PhoneNumber(fromWhatsApp),""
                    ).setContentSid("HXd6f530e73cb11f609eb8c11df3ab6c45") 
             .setContentVariables(variablesJson).create();

            System.out.println("✅ WhatsApp message sent! SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("❌ Error sending WhatsApp message: " + e.getMessage());
}

    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String normalizeToWhatsApp(String toPhoneNumber) {
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) return "";
        String digits = toPhoneNumber.replaceAll("[^0-9+]", "");
        if (digits.startsWith("whatsapp:")) return digits; // already prefixed
        if (digits.startsWith("+")) return "whatsapp:" + digits;
        if (digits.matches("[0-9]{10}")) return "whatsapp:+91" + digits; // assume India if bare 10 digits
        return "whatsapp:+" + digits;
    }
    
    public void sendAppointmentUpdate(String toPhoneNumber, String patientName, String status, String dateTime) {
        try {
            Twilio.init(accountSid, authToken);
            String to = normalizeToWhatsApp(toPhoneNumber);
            String from = fromWhatsApp.startsWith("whatsapp:") ? fromWhatsApp : "whatsapp:" + fromWhatsApp;

            // Build variables JSON (matches template placeholders)
            Map<String, String> vars = Map.of(
                "1", patientName,
                "2", status,
                "3", dateTime
            );
            String varsJson = objectMapper.writeValueAsString(vars);

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromWhatsApp),
                    "" // body not needed for template
            )
            .setContentSid(appointmentUpdateTemplateSid)
            .setContentVariables(varsJson)
            .create();

            System.out.println("✅ Appointment update sent, SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("❌ Failed to send appointment update: " + e.getMessage());
        }
    }
    /**
     * Send a pre-approved template that also attaches a media/document (mediaUrl must be https accessible).
     * variables keys should be "1","2",...
     */
    public void sendFileTemplate(String toPhoneNumber,
                             String templateSid,
                             String patientName,
                             String feature,       // Bill, Prescription, Report, etc.
                             String fileName,
                             String fileType,
                             String mediaUrl) {
    try {
        Twilio.init(accountSid, authToken);
        String to = normalizeToWhatsApp(toPhoneNumber);
        String from = fromWhatsApp != null && fromWhatsApp.toLowerCase().startsWith("whatsapp:")
                ? fromWhatsApp
                : ("whatsapp:" + fromWhatsApp);

        // Map variables to match Twilio template placeholders
        Map<String, String> variables = new HashMap<>();
        variables.put("1", patientName);   // {{1}}
        variables.put("2", feature);       // {{2}}
        variables.put("3", fileName);      // {{3}}
        variables.put("4", fileType);      // {{4}}
        variables.put("5", mediaUrl);      // {{5}} Media URL

        String varsJson = objectMapper.writeValueAsString(variables);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                "" // Body is filled by template
        ).setContentSid(templateSid)
         .setContentVariables(varsJson)
         .create();

        System.out.println("✅ WhatsApp file template sent with media, SID: " + message.getSid());
    } catch (Exception e) {
        System.err.println("❌ Failed to send WhatsApp file template: " + e.getMessage());
    }
}

}

