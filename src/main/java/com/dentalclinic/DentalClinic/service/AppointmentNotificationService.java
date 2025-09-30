package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;

public interface AppointmentNotificationService {
    
    /**
     * Send appointment scheduled notification
     */
    void sendAppointmentScheduledNotification(Appointment appointment);
    
    /**
     * Send appointment reminder notification
     */
    void sendAppointmentReminderNotification(Appointment appointment);
    
    /**
     * Send appointment rescheduled notification
     */
    void sendAppointmentRescheduledNotification(Appointment appointment);
    
    /**
     * Send appointment cancelled notification
     */
    void sendAppointmentCancelledNotification(Appointment appointment);
}
