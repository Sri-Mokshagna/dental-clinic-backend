package com.dentalclinic.DentalClinic.controller;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Report;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.service.EmailService;
import com.dentalclinic.DentalClinic.service.ExpenseService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.ReportService;
import com.dentalclinic.DentalClinic.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final UserService userService;
    private final PatientService patientService;
    private final ExpenseService expenseService;
    private final ReportService reportService;
    private final AppointmentService appointmentService;
    private final EmailService emailService;

    public DoctorController(UserService userService,
                            PatientService patientService,
                            ExpenseService expenseService,
                            ReportService reportService,AppointmentService appointmentService,EmailService emailService) {
        this.userService = userService;
        this.patientService = patientService;
        this.expenseService = expenseService;
        this.reportService = reportService;
        this.appointmentService = appointmentService;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model, Principal principal) {
        // ✅ Logged-in doctor
    	User doctor = userService.findByUsernameOrPhone(principal.getName())
    	        .orElseThrow(() -> new RuntimeException("Doctor not found"));


        // ✅ Today's Appointments (for this doctor)
        List<Appointment> todaysAppointments = appointmentService.findAppointmentsForDoctorToday(doctor);
        model.addAttribute("appointments", todaysAppointments);
        
        List<Expense> todaysExpenses = expenseService.findByAddedByAndDate(doctor, LocalDate.now());
        model.addAttribute("expenses", todaysExpenses);

        return "doctor/dashboard";
    }

    // ✅ Add Patient
    @GetMapping("/add-patient")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "doctor/add-patient";
    }

    @PostMapping("/add-patient")
    public String addPatient(@ModelAttribute Patient patient, Authentication auth) {
    	User doctor = userService.findByUsernameOrPhone(auth.getName()).orElseThrow();

    	patientService.addOrLinkPatient(patient, doctor.getId());
        return "redirect:/doctor/patients";
    }

    // ✅ View Patients
    @GetMapping("/patients")
    public String viewPatients(Authentication auth, Model model) {
    	User doctor = userService.findByUsernameOrPhone(auth.getName()).orElseThrow();

        model.addAttribute("patients", patientService.findByDoctor(doctor));
        return "doctor/patients";
    }

    // ✅ Update Medical Info & Treatment
    @PostMapping("/update-patient/{id}")
    public String updatePatient(@PathVariable Long id,
                                @RequestParam String medicalInfo,
                                @RequestParam double treatmentAmount) {
        patientService.updatePatientInfo(id, medicalInfo, treatmentAmount);
        return "redirect:/doctor/patients";
    }

    // ✅ Add Expense (Doctor cannot view totals)
    @GetMapping("/add-expense")
    public String addExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "doctor/add-expense";
    }

    @PostMapping("/add-expense")
    public String addExpense(@ModelAttribute Expense expense, Authentication auth) {
    	User doctor = userService.findByUsernameOrPhone(auth.getName()).orElseThrow();

        expense.setAddedBy(doctor);
        expenseService.save(expense);
        return "redirect:/doctor/dashboard";
    }

    // ✅ Upload Report
    @GetMapping("/upload-report")
    public String uploadReportForm(Model model) {
        model.addAttribute("report", new Report());
        return "doctor/upload-report";
    }

    @PostMapping("/upload-report")
    public String uploadReport(@ModelAttribute Report report,
                               @RequestParam Long patientId,
                               Authentication auth) {
    	User doctor = userService.findByUsernameOrPhone(auth.getName()).orElseThrow();

        Patient patient = patientService.findById(patientId).orElseThrow();

        report.setDoctor(doctor);
        report.setPatient(patient);
        reportService.save(report);
        return "redirect:/doctor/dashboard";
    }
    
}
