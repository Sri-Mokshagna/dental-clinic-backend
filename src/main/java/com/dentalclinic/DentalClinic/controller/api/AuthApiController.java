package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.User;
 
import com.dentalclinic.DentalClinic.service.UserService;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthApiController {

    @Autowired
    private UserService userService;

    

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            // Disable self-registration of patients
            return ResponseEntity.status(403).body(Map.of("error", "Self-registration is disabled. Please contact clinic staff."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String identifier = request.get("username"); // username or phone
            String password = request.get("password");

            if (identifier == null || identifier.isBlank() || password == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Username and password are required"));
            }

            Optional<User> userOpt = userService.findByUsernameOrPhone(identifier);
            if (userOpt.isEmpty() && identifier.contains("@")) {
                // Try email as a fallback
                userOpt = userService.findByEmail(identifier);
            }
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            User user = userOpt.get();

            boolean passwordMatches = false;
            // Primary: bcrypt match
            if (user.getPassword() != null && passwordEncoder.matches(password, user.getPassword())) {
                passwordMatches = true;
            } else {
                // Fallback: legacy plain-text password support (for existing DB rows)
                passwordMatches = password.equals(user.getPassword());
            }

            if (!passwordMatches) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "fullName", user.getFullName(),
                "role", user.getRole().toString()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
}
