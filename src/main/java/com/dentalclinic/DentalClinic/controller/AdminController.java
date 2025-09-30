package com.dentalclinic.DentalClinic.controller; 

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.service.ExpenseService;
import com.dentalclinic.DentalClinic.service.PatientService;
import com.dentalclinic.DentalClinic.service.SalaryService;
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
    private final UserService userService;
    private final ExpenseService expenseService;
    private final SalaryService salaryService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final WhatsAppSender whatsAppSender; 

    public AdminController(UserService userService, ExpenseService expenseService,
                           SalaryService salaryService, PatientService patientService,
                           AppointmentService appointmentService, WhatsAppSender whatsAppSender) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.salaryService = salaryService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.whatsAppSender = whatsAppSender;
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("doctorCount", userService.findByRole(Role.DOCTOR).size());
        model.addAttribute("staffCount", userService.findByRole(Role.STAFF).size());
        model.addAttribute("patientCount", patientService.findAll().size());
        return "admin/dashboard";
    }


    
 // View all patients
    @GetMapping("/manage-patients")
    public String managePatients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "admin/manage-patients";
    }

    // View all appointments
    @GetMapping("/manage-appointments")
    public String manageAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        return "admin/manage-appointments";
    }
    

    // Manage salaries
    @GetMapping("/salaries")
    public String manageSalaries(Model model) {
        model.addAttribute("salaries", salaryService.findAll());
        return "admin/salaries";
    }

    @PostMapping("/add-salary")
    public String addSalary(@RequestParam Long staffId, @RequestParam double amount) {
        salaryService.addSalary(staffId, amount);
        return "redirect:/admin/salaries";
    }


    // Delete any user
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/manage-doctors";
    }
    
    @GetMapping("/send-promotions")
    public String getSendPromotions() {
    	return "admin/send-promotions";
    }
    @PostMapping("/send-promotions")
    public String sendPromotions(@RequestParam String message, RedirectAttributes redirectAttributes) {
        List<Patient> patients = patientService.findAll();
        for (Patient patient : patients) {
            if (patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty()) {
                System.out.println(patient.getPhoneNumber());
                String fullName = patient.getFullName() != null ? patient.getFullName() : "Patient";
                whatsAppSender.sendMessage(patient.getPhoneNumber(), message, fullName);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Promotions sent successfully to all patients!");
        return "redirect:/admin/dashboard";
    }



    @GetMapping("/expenses")
    public String expenses(Model model) {
    	LocalDate today = LocalDate.now();

        // Start of month (as LocalDate)
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // End of month (as LocalDate)
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());


        List<Expense> monthlyExpenses = expenseService.findByDateRange(startOfMonth, endOfMonth);

        double totalMonthlyExpense = monthlyExpenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expenses", monthlyExpenses);
        model.addAttribute("expense", new Expense()); // form object
        model.addAttribute("totalExpense", totalMonthlyExpense);

        return "admin/expenses";
    }

    // Add Expense (same page)
    @PostMapping("/expenses")
    public String addExpense(@ModelAttribute Expense expense, RedirectAttributes redirectAttributes) {
        expenseService.save(expense);
        redirectAttributes.addFlashAttribute("success", "Expense added successfully!");
        return "redirect:/admin/expenses"; // reload same page
    }

    // Approve Expense
    @PostMapping("/expenses/approve/{id}")
    public String approveExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        expenseService.approveExpense(id);
        redirectAttributes.addFlashAttribute("success", "Expense approved!");
        return "redirect:/admin/expenses";
    }

    // Delete Expense
    @PostMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        expenseService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Expense deleted!");
        return "redirect:/admin/expenses";
    }

    // Optional: simpler entry to statistics page (you already have /statistics)
    @GetMapping("/reports")
    public String reportsRedirect() {
        return "admin/reports";
    }
    @GetMapping("/manage-doctors")
    public String manageDoctors(Model model) {
        model.addAttribute("doctor", new User()); // empty form object
        model.addAttribute("doctors", userService.findByRole(Role.DOCTOR));
        return "admin/manage-doctors";
    }

    @PostMapping("/add-doctor")
    public String addDoctor(@ModelAttribute("doctor") User doctor) {
        doctor.setRole(Role.DOCTOR);
        doctor.setEnabled(true); // Ensure doctor is enabled
        userService.save(doctor);
        return "redirect:/admin/manage-doctors";
    }

    @GetMapping("/manage-staff")
    public String manageStaff(Model model) {
        model.addAttribute("staff", new User()); // empty form object
        model.addAttribute("staffList", userService.findByRole(Role.STAFF));
        return "admin/manage-staff";
    }

    @PostMapping("/add-staff")
    public String addStaff(@ModelAttribute("staff") User staff) {
        staff.setRole(Role.STAFF);
        staff.setEnabled(true); // Ensure staff is enabled
        userService.save(staff);
        return "redirect:/admin/manage-staff";
    }

}
