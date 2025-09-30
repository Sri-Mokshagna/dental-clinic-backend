package com.dentalclinic.DentalClinic.controller;

 

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dentalclinic.DentalClinic.model.Feedback;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
 
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.service.impl.AppointmentServiceImpl;
import com.dentalclinic.DentalClinic.service.impl.FeedbackServiceImpl;
import com.dentalclinic.DentalClinic.service.impl.PatientServiceImpl;
import com.dentalclinic.DentalClinic.service.impl.ReportServiceImpl;
@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final AppointmentServiceImpl appointmentService;
    private final ReportServiceImpl reportService;
    private final FeedbackServiceImpl feedbackService;
    private final PatientServiceImpl patientService;
    private final UserService userService;

    public PatientController(AppointmentServiceImpl appointmentService,
                             ReportServiceImpl reportService,
                             FeedbackServiceImpl feedbackService,
                             PatientServiceImpl patientService,UserService userService) {
        this.appointmentService = appointmentService;
        this.reportService = reportService;
        this.feedbackService = feedbackService;
        this.patientService = patientService;
        this.userService = userService;
    }

    // Dashboard

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        // 1️⃣ Get phone number from authenticated principal
        String phoneNumber = auth.getName(); // assuming phoneNumber is used as principal

        // 2️⃣ Fetch the User entity
        User user = userService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Fetch the Patient by phone number (no direct linkage)
        Patient patient = patientService.findByPhoneNumber(user.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Patient record not found"));

        // 4️⃣ Add to model
        model.addAttribute("patient", patient);

        // Optional: add extra info like appointments, reports, etc.
        // model.addAttribute("appointmentCount", appointmentService.countByPatient(patient));
        // model.addAttribute("totalExpenses", reportService.calculateTotalExpenses(patient));
        // model.addAttribute("reportCount", reportService.countByPatient(patient));

        return "patient/dashboard";
    }


    // Appointments
    @GetMapping("/appointments")
    public String appointments(Authentication auth, Model model) {
        String username = auth.getName();
        Patient patient = patientService.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("appointments", appointmentService.findByPatient(patient));
        return "patient/appointments";
    }

    // Reports
    @GetMapping("/reports")
    public String reports(Authentication auth, Model model) {
        String username = auth.getName();
        Patient patient = patientService.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        model.addAttribute("reports", reportService.findByPatient(patient));
        return "patient/reports";
    }

    // Feedback
    @PostMapping("/feedback")
    public String feedback(@ModelAttribute Feedback feedback, Authentication auth) {
        String username = auth.getName();
        Patient patient = patientService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        feedback.setPatient(patient);
        feedbackService.save(feedback);
        return "redirect:/patient/dashboard";
    }
}
