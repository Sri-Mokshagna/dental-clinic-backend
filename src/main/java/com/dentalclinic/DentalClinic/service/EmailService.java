package com.dentalclinic.DentalClinic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWhatsAppSandboxLink(String toEmail, String patientName) {
        String subject = "Confirm WhatsApp Notifications - Dental Clinic";
        String link = "https://wa.me/14155238886?text=join%20forgot-volume";
        String text = "Hello " + patientName + ",\n\n"
                + "To start receiving appointment reminders & promotions on WhatsApp, "
                + "please click the link below and send the pre-filled message:\n\n"
                + link + "\n\n"
                + "Once you send this message, you’ll be registered to receive WhatsApp notifications from us.\n\n"
                + "Thank you,\nDental Clinic";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
