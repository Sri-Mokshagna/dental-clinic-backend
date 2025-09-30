package com.dentalclinic.DentalClinic.util;

import org.springframework.stereotype.Component;

@Component
public class WhatsAppTemplates {
    
    /**
     * Template for file sharing notification
     */
    public String getFileSharingTemplate(String patientName, String fileName, String fileType, String downloadLink) {
        return String.format("""
            🏥 *Dental Clinic - File Shared*
            
            Dear *%s*,
            
            A new file has been shared with you:
            
            📄 *File Name:* %s
            📋 *File Type:* %s
            
            🔗 *Download Link:* %s
            
            Please download and review the file at your convenience.
            
            If you have any questions, please contact us.
            
            Best regards,
            Dental Clinic Team
            """, patientName, fileName, fileType, downloadLink);
    }
    
    /**
     * Template for appointment scheduling notification
     */
    public String getAppointmentScheduledTemplate(String patientName, String appointmentDate, String appointmentTime, String doctorName, String clinicAddress) {
        return String.format("""
            🏥 *Dental Clinic - Appointment Scheduled*
            
            Dear *%s*,
            
            Your appointment has been successfully scheduled:
            
            📅 *Date:* %s
            ⏰ *Time:* %s
            👨‍⚕️ *Doctor:* Dr. %s
            
            📍 *Clinic Address:* %s
            
            Please arrive 10 minutes before your scheduled time.
            
            If you need to reschedule or cancel, please contact us at least 24 hours in advance.
            
            We look forward to seeing you!
            
            Best regards,
            Dental Clinic Team
            """, patientName, appointmentDate, appointmentTime, doctorName, clinicAddress);
    }
    
    /**
     * Template for appointment reminder (24 hours before)
     */
    public String getAppointmentReminderTemplate(String patientName, String appointmentDate, String appointmentTime, String doctorName) {
        return String.format("""
            🏥 *Dental Clinic - Appointment Reminder*
            
            Dear *%s*,
            
            This is a friendly reminder about your upcoming appointment:
            
            📅 *Date:* %s
            ⏰ *Time:* %s
            👨‍⚕️ *Doctor:* Dr. %s
            
            Please arrive 10 minutes before your scheduled time.
            
            If you need to reschedule or cancel, please contact us immediately.
            
            Best regards,
            Dental Clinic Team
            """, patientName, appointmentDate, appointmentTime, doctorName);
    }
    
    /**
     * Template for bill payment notification
     */
    public String getBillPaymentTemplate(String patientName, String billId, String amount, String paymentMethod, String paymentDate) {
        return String.format("""
            🏥 *Dental Clinic - Payment Confirmation*
            
            Dear *%s*,
            
            Your payment has been successfully recorded:
            
            🧾 *Bill ID:* %s
            💰 *Amount:* ₹%s
            💳 *Payment Method:* %s
            📅 *Payment Date:* %s
            
            Thank you for your payment. A receipt has been generated for your records.
            
            If you have any questions about this payment, please contact us.
            
            Best regards,
            Dental Clinic Team
            """, patientName, billId, amount, paymentMethod, paymentDate);
    }
    
    /**
     * Template for new bill notification
     */
    public String getNewBillTemplate(String patientName, String billId, String amount, String dueDate, String services) {
        return String.format("""
            🏥 *Dental Clinic - New Bill Generated*
            
            Dear *%s*,
            
            A new bill has been generated for your recent visit:
            
            🧾 *Bill ID:* %s
            💰 *Amount:* ₹%s
            📅 *Due Date:* %s
            
            📋 *Services Provided:*
            %s
            
            Please make payment at your earliest convenience.
            
            If you have any questions about this bill, please contact us.
            
            Best regards,
            Dental Clinic Team
            """, patientName, billId, amount, dueDate, services);
    }
    
    /**
     * Template for medication reminder
     */
    public String getMedicationReminderTemplate(String patientName, String medicationName, String dosage, String frequency) {
        return String.format("""
            🏥 *Dental Clinic - Medication Reminder*
            
            Dear *%s*,
            
            This is a reminder to take your medication:
            
            💊 *Medication:* %s
            📏 *Dosage:* %s
            ⏰ *Frequency:* %s
            
            Please follow the prescribed dosage and timing.
            
            If you have any questions about your medication, please contact us.
            
            Best regards,
            Dental Clinic Team
            """, patientName, medicationName, dosage, frequency);
    }
    
    /**
     * Template for general notification
     */
    public String getGeneralNotificationTemplate(String patientName, String message) {
        return String.format("""
            🏥 *Dental Clinic - Notification*
            
            Dear *%s*,
            
            %s
            
            If you have any questions, please contact us.
            
            Best regards,
            Dental Clinic Team
            """, patientName, message);
    }
}
