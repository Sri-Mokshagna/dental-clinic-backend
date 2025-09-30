package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.service.AppointmentNotificationService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;
import com.dentalclinic.DentalClinic.util.WhatsAppTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppointmentNotificationServiceImpl implements AppointmentNotificationService {
    
    @Autowired
    private WhatsAppSender whatsAppSender;
    
    @Autowired
    private WhatsAppTemplates whatsAppTemplates;

    @Value("${twilio.whatsapp.template.appointmentUpdate}")
    private String appointmentTemplateSid;

    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    
    @Override
public void sendAppointmentScheduledNotification(Appointment appointment) {
    try {
        Patient patient = appointment.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) return;

        String appointmentDateTime = appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Map<String, String> vars = new HashMap<>();
        vars.put("1", patient.getFullName());
        vars.put("2", "scheduled");
        vars.put("3", appointmentDateTime);

        whatsAppSender.sendTemplate(patient.getPhoneNumber(), appointmentTemplateSid, vars);
    } catch (Exception e) {
        System.err.println("Failed to send appointment scheduled notification: " + e.getMessage());
    }
}

    
    @Override
    public void sendAppointmentReminderNotification(Appointment appointment) {
        try {
        Patient patient = appointment.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) return;

        String appointmentDateTime = appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Map<String, String> vars = new HashMap<>();
        vars.put("1", patient.getFullName());
        vars.put("2", "scheduled");
        vars.put("3", appointmentDateTime);

        whatsAppSender.sendTemplate(patient.getPhoneNumber(), appointmentTemplateSid, vars);
    } catch (Exception e) {
        System.err.println("Failed to send appointment scheduled notification: " + e.getMessage());
    }
    }
    
    @Override
    public void sendAppointmentRescheduledNotification(Appointment appointment) {
        try {
        Patient patient = appointment.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) return;

        String appointmentDateTime = appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Map<String, String> vars = new HashMap<>();
        vars.put("1", patient.getFullName());
        vars.put("2", "rescheduled");
        vars.put("3", appointmentDateTime);

        whatsAppSender.sendTemplate(patient.getPhoneNumber(), appointmentTemplateSid, vars);
    } catch (Exception e) {
        System.err.println("Failed to send appointment scheduled notification: " + e.getMessage());
    }
    }
    
    @Override
    public void sendAppointmentCancelledNotification(Appointment appointment) {
        try {
        Patient patient = appointment.getPatient();
        if (patient == null || patient.getPhoneNumber() == null) return;

        String appointmentDateTime = appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Map<String, String> vars = new HashMap<>();
        vars.put("1", patient.getFullName());
        vars.put("2", "cancelled");
        vars.put("3", appointmentDateTime);

        whatsAppSender.sendTemplate(patient.getPhoneNumber(), appointmentTemplateSid, vars);
    } catch (Exception e) {
        System.err.println("Failed to send appointment scheduled notification: " + e.getMessage());
    }
    }
}
