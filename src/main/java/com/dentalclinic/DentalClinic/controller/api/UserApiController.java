package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            List<Map<String, Object>> userData = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(userOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<User> users = userService.findByRole(userRole);
            List<Map<String, Object>> userData = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch users by role: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        try {
            User user = new User();
            user.setUsername((String) request.get("username"));
            user.setPassword((String) request.get("password")); // Don't encode here, let UserService handle it
            user.setEmail((String) request.get("email"));
            user.setPhoneNumber((String) request.get("phoneNumber"));
            user.setFullName((String) request.get("fullName"));
            user.setEnabled((Boolean) request.getOrDefault("enabled", true));
            
            // Set role
            String roleStr = (String) request.get("role");
            if (roleStr != null) {
                user.setRole(Role.valueOf(roleStr.toUpperCase()));
            }

            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertToResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            
            if (request.get("username") != null) {
                user.setUsername((String) request.get("username"));
            }
            if (request.get("password") != null) {
                user.setPassword(passwordEncoder.encode((String) request.get("password")));
            }
            if (request.get("email") != null) {
                user.setEmail((String) request.get("email"));
            }
            if (request.get("phoneNumber") != null) {
                user.setPhoneNumber((String) request.get("phoneNumber"));
            }
            if (request.get("fullName") != null) {
                user.setFullName((String) request.get("fullName"));
            }
            if (request.get("enabled") != null) {
                user.setEnabled((Boolean) request.get("enabled"));
            }
            if (request.get("role") != null) {
                String roleStr = (String) request.get("role");
                user.setRole(Role.valueOf(roleStr.toUpperCase()));
            }

            User savedUser = userService.save(user);
            return ResponseEntity.ok(convertToResponse(savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole().toString());
        response.put("enabled", user.isEnabled());
        
        return response;
    }
}
