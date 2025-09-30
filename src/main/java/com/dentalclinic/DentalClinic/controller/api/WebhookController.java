package com.dentalclinic.DentalClinic.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patient-otp")
@CrossOrigin(origins = "*")
public class WebhookController {

    /**
     * Handles SMS status callbacks from Twilio
     * This endpoint receives delivery status updates for SMS messages
     */
    @PostMapping("/sms-status")
    public ResponseEntity<?> handleSMSStatus(@RequestParam Map<String, String> params) {
        try {
            String messageSid = params.get("MessageSid");
            String messageStatus = params.get("MessageStatus");
            String to = params.get("To");
            String from = params.get("From");
            
            System.out.println("📱 SMS Status Update:");
            System.out.println("   Message SID: " + messageSid);
            System.out.println("   Status: " + messageStatus);
            System.out.println("   To: " + to);
            System.out.println("   From: " + from);
            
            // Log different statuses
            switch (messageStatus) {
                case "delivered":
                    System.out.println("✅ SMS delivered successfully!");
                    break;
                case "failed":
                    System.out.println("❌ SMS delivery failed!");
                    break;
                case "undelivered":
                    System.out.println("⚠️ SMS undelivered!");
                    break;
                case "sent":
                    System.out.println("📤 SMS sent (delivery pending)");
                    break;
                default:
                    System.out.println("ℹ️ SMS status: " + messageStatus);
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            System.err.println("❌ Error handling SMS status: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Handles WhatsApp status callbacks from Twilio
     * This endpoint receives delivery status updates for WhatsApp messages
     */
    @PostMapping("/whatsapp-status")
    public ResponseEntity<?> handleWhatsAppStatus(@RequestParam Map<String, String> params) {
        try {
            String messageSid = params.get("MessageSid");
            String messageStatus = params.get("MessageStatus");
            String to = params.get("To");
            String from = params.get("From");
            
            System.out.println("💬 WhatsApp Status Update:");
            System.out.println("   Message SID: " + messageSid);
            System.out.println("   Status: " + messageStatus);
            System.out.println("   To: " + to);
            System.out.println("   From: " + from);
            
            // Log different statuses
            switch (messageStatus) {
                case "delivered":
                    System.out.println("✅ WhatsApp message delivered successfully!");
                    break;
                case "failed":
                    System.out.println("❌ WhatsApp message delivery failed!");
                    break;
                case "undelivered":
                    System.out.println("⚠️ WhatsApp message undelivered!");
                    break;
                case "sent":
                    System.out.println("📤 WhatsApp message sent (delivery pending)");
                    break;
                default:
                    System.out.println("ℹ️ WhatsApp status: " + messageStatus);
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            System.err.println("❌ Error handling WhatsApp status: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Handles incoming messages from Twilio
     * This endpoint receives incoming SMS/WhatsApp messages
     */
    @PostMapping("/incoming-message")
    public ResponseEntity<?> handleIncomingMessage(@RequestParam Map<String, String> params) {
        try {
            String messageSid = params.get("MessageSid");
            String from = params.get("From");
            String to = params.get("To");
            String body = params.get("Body");
            String numMedia = params.get("NumMedia");
            
            System.out.println("📨 Incoming Message:");
            System.out.println("   Message SID: " + messageSid);
            System.out.println("   From: " + from);
            System.out.println("   To: " + to);
            System.out.println("   Body: " + body);
            System.out.println("   Media Count: " + numMedia);
            
            // Handle different types of messages
            if (body != null && !body.trim().isEmpty()) {
                String lowerBody = body.toLowerCase().trim();
                
                if (lowerBody.contains("otp") || lowerBody.contains("code")) {
                    System.out.println("🔐 OTP-related message received");
                    // You can add logic here to handle OTP verification
                } else if (lowerBody.contains("help") || lowerBody.contains("info")) {
                    System.out.println("❓ Help request received");
                    // You can add logic here to send help information
                } else {
                    System.out.println("💬 General message received");
                }
            }
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            System.err.println("❌ Error handling incoming message: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Health check endpoint for webhook testing
     */
    @GetMapping("/webhook-health")
    public ResponseEntity<?> webhookHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "message", "Webhook endpoints are working",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
