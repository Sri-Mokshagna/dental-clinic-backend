package com.dentalclinic.DentalClinic.controller.api;

import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.service.ExpenseService;
import com.dentalclinic.DentalClinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseApiController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllExpenses() {
        try {
            List<Expense> expenses = expenseService.findAll();
            System.out.println("Total expenses found: " + expenses.size()); // Debug log
            for (Expense exp : expenses) {
                System.out.println("Expense: " + exp.getDescription() + " - Approved: " + exp.isApproved() + " - Added by: " + (exp.getAddedBy() != null ? exp.getAddedBy().getFullName() : "Unknown")); // Debug log
            }
            List<Map<String, Object>> expenseData = expenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(expenseData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch expenses: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingExpenses() {
        try {
            List<Expense> pendingExpenses = expenseService.findPendingApproval();
            System.out.println("Found " + pendingExpenses.size() + " pending expenses"); // Debug log
            for (Expense exp : pendingExpenses) {
                System.out.println("Pending expense: " + exp.getDescription() + " by " + (exp.getAddedBy() != null ? exp.getAddedBy().getFullName() : "Unknown")); // Debug log
            }
            List<Map<String, Object>> expenseData = pendingExpenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(expenseData);
        } catch (Exception e) {
            System.out.println("Error fetching pending expenses: " + e.getMessage()); // Debug log
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch pending expenses: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id) {
        try {
            Optional<Expense> expenseOpt = expenseService.findById(id);
            if (expenseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToResponse(expenseOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch expense: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Creating expense with request: " + request); // Debug log
            
            Expense expense = new Expense();
            expense.setDescription((String) request.get("description"));
            expense.setAmount(((Number) request.get("amount")).doubleValue());
            
            // Parse date
            String dateStr = (String) request.get("date");
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            expense.setDate(date);
            
            expense.setApproved((Boolean) request.getOrDefault("approved", false));

            // Link to user who added the expense
            if (request.get("addedById") != null) {
                Long addedById = ((Number) request.get("addedById")).longValue();
                System.out.println("Looking for user with ID: " + addedById); // Debug log
                Optional<User> userOpt = userService.findById(addedById);
                if (userOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "User not found with ID: " + addedById));
                }
                expense.setAddedBy(userOpt.get());
                System.out.println("Linked expense to user: " + userOpt.get().getFullName()); // Debug log
            } else {
                System.out.println("No addedById provided in request"); // Debug log
                return ResponseEntity.badRequest().body(Map.of("error", "addedById is required"));
            }

            Expense savedExpense = expenseService.save(expense);
            System.out.println("Saved expense with ID: " + savedExpense.getId() + ", approved: " + savedExpense.isApproved()); // Debug log
            return ResponseEntity.ok(convertToResponse(savedExpense));
        } catch (Exception e) {
            System.out.println("Error creating expense: " + e.getMessage()); // Debug log
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create expense: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<Expense> expenseOpt = expenseService.findById(id);
            if (expenseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Expense expense = expenseOpt.get();
            
            if (request.get("description") != null) {
                expense.setDescription((String) request.get("description"));
            }
            if (request.get("amount") != null) {
                expense.setAmount(((Number) request.get("amount")).doubleValue());
            }
            if (request.get("date") != null) {
                String dateStr = (String) request.get("date");
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                expense.setDate(date);
            }
            if (request.get("approved") != null) {
                expense.setApproved((Boolean) request.get("approved"));
            }

            // Update added by user
            if (request.get("addedById") != null) {
                Long addedById = ((Number) request.get("addedById")).longValue();
                Optional<User> userOpt = userService.findById(addedById);
                if (userOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
                }
                expense.setAddedBy(userOpt.get());
            }

            Expense savedExpense = expenseService.save(expense);
            return ResponseEntity.ok(convertToResponse(savedExpense));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update expense: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete expense: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveExpense(@PathVariable Long id) {
        try {
            expenseService.approveExpense(id);
            return ResponseEntity.ok(Map.of("message", "Expense approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to approve expense: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectExpense(@PathVariable Long id) {
        try {
            expenseService.rejectExpense(id);
            return ResponseEntity.ok(Map.of("message", "Expense rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to reject expense: " + e.getMessage()));
        }
    }

    private Map<String, Object> convertToResponse(Expense expense) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", expense.getId());
        response.put("description", expense.getDescription());
        response.put("amount", expense.getAmount());
        response.put("date", expense.getDate().toString());
        response.put("approved", expense.isApproved());
        
        if (expense.getAddedBy() != null) {
            response.put("addedBy", Map.of(
                "id", expense.getAddedBy().getId(),
                "fullName", expense.getAddedBy().getFullName(),
                "username", expense.getAddedBy().getUsername()
            ));
        }
        
        return response;
    }
}
