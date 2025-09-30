package com.dentalclinic.DentalClinic.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.service.ExpenseService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffController {

    private final AppointmentService appointmentService;
    private final ExpenseService expenseService;
    private final PatientService patientService;
    private final UserService userService;

    public StaffController(AppointmentService appointmentService,
                           ExpenseService expenseService,
                           PatientService patientService,
                           UserService userService) {
        this.appointmentService = appointmentService;
        this.expenseService = expenseService;
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String staffDashboard(Model model, Principal principal) {
        // ✅ Logged-in staff
        User staff = userService.findByUsernameOrPhone(principal.getName())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // ✅ Today's Appointments (for this staff)
        List<Appointment> todaysAppointments = appointmentService.findAppointmentsForStaffToday(staff);
        model.addAttribute("appointments", todaysAppointments);

        // ✅ Today's Expenses
        List<Expense> todaysExpenses = expenseService.findByAddedByAndDate(staff, LocalDate.now());
        model.addAttribute("expenses", todaysExpenses);

        return "staff/dashboard";
    }



    // Add Appointment form
    @GetMapping("/add-appointment")
    public String showAddAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("doctors", userService.findDoctors());
        return "staff/add-appointment";
    }

    @PostMapping("/add-appointment")
    public String saveAppointment(@ModelAttribute("appointment") Appointment appointment,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            // ✅ Resolve Patient
            Long patientId = appointment.getPatient().getId();
            Patient patient = patientService.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Invalid patient ID: " + patientId));
            appointment.setPatient(patient);

            // ✅ Resolve Doctor
            Long doctorId = appointment.getDoctor().getId();
            User doctor = userService.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Invalid doctor ID: " + doctorId));
            appointment.setDoctor(doctor);

            // ✅ Logged-in staff
            User staff = userService.findByUsernameOrPhone(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            appointment.setStaff(staff);
            // Save
            appointmentService.save(appointment);

            redirectAttributes.addFlashAttribute("success", "Appointment saved successfully!");
            return "redirect:/staff/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving appointment: " + e.getMessage());
            return "redirect:/staff/add-appointment";
        }
    }



 // Add Expense
    @GetMapping("/add-expense")
    public String addExpensePage(Model model) {
        model.addAttribute("expense", new Expense());
        return "staff/add-expense";
    }

    @PostMapping("/add-expense")
    public String addExpense(@ModelAttribute Expense expense, Authentication auth) {
        // ✅ Get the logged-in username
        String username = auth.getName();

        // ✅ Fetch your actual User entity from DB
        User staff = userService.findByUsernameOrPhone(auth.getName())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // ✅ Set staff and default values
        expense.setAddedBy(staff);
        expense.setApproved(false);

        // ✅ Save to DB
        expenseService.save(expense);

        return "redirect:/staff/dashboard";
    }

}
