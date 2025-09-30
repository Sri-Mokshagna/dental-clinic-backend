package com.dentalclinic.DentalClinic.controller;

import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.service.PatientService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService; // <— ADDED

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               HttpServletRequest request,
                               Model model) {
        String rawPassword = user.getPassword();
        user.setRole(Role.PATIENT);

        // 🚨 Check if phone already exists
        if (userService.phoneExists(user.getPhoneNumber())) {
            model.addAttribute("error", "Phone number already registered. Please login.");
            return "auth/register"; // back to register page with error
        }

        // save user
        userService.save(user);

        // ✅ Link with existing patient if doctor already added (by phone)
        patientService.linkOrCreatePatient(user);

        try {
            request.login(user.getUsername(), rawPassword);
        } catch (ServletException e) {
            return "redirect:/auth/login?error";
        }
        return "redirect:/auth/default";
    }



    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        String username = authentication.getName();

        // Fetch User entity
        User user = userService.findByUsernameOrPhone(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 Ensure patient record exists when role is PATIENT (by phone number, no direct linkage)
        if (user.getRole() == Role.PATIENT) {
            patientService.findByPhoneNumber(user.getPhoneNumber()).orElseGet(() -> {
                Patient patient = new Patient();
                patient.setFullName(user.getFullName());
                patient.setPhoneNumber(user.getPhoneNumber());
                return patientService.save(patient);
            });
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            return "redirect:/doctor/dashboard";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
            return "redirect:/staff/dashboard";
        } else {
            return "redirect:/patient/dashboard";
        }
    }


    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/auth/login?logout"; // redirect to login page with logout msg
    }
}
