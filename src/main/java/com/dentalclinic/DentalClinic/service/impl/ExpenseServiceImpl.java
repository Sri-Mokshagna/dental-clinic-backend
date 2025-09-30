package com.dentalclinic.DentalClinic.service.impl;

import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.ExpenseRepository;
import com.dentalclinic.DentalClinic.service.ExpenseService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense save(Expense expense) {
        // Only set date to today if no date is provided
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());
        }
        
        // Auto-approve expenses added by admin, require approval for doctor/staff
        if (expense.getAddedBy() != null && expense.getAddedBy().getRole().toString().equals("ADMIN")) {
            expense.setApproved(true);
            System.out.println("ExpenseService: Auto-approving expense for ADMIN user: " + expense.getAddedBy().getFullName()); // Debug log
        } else {
            expense.setApproved(false);
            System.out.println("ExpenseService: Setting expense to pending for user: " + (expense.getAddedBy() != null ? expense.getAddedBy().getFullName() + " (Role: " + expense.getAddedBy().getRole() + ")" : "Unknown")); // Debug log
        }
        
        Expense saved = expenseRepository.save(expense);
        System.out.println("ExpenseService: Saved expense ID " + saved.getId() + " - Approved: " + saved.isApproved()); // Debug log
        return saved;
    }

    @Override
    public List<Expense> findByUser(User user) {
        return expenseRepository.findByAddedBy(user);
    }

    @Override
    public List<Expense> findPendingApproval() {
        List<Expense> pending = expenseRepository.findByApproved(false);
        System.out.println("ExpenseService: Found " + pending.size() + " pending expenses"); // Debug log
        for (Expense exp : pending) {
            System.out.println("Pending: " + exp.getDescription() + " by " + (exp.getAddedBy() != null ? exp.getAddedBy().getFullName() : "Unknown")); // Debug log
        }
        return pending;
    }

    @Override
    public List<Expense> findByDateRange(LocalDate start, LocalDate end) {
        return expenseRepository.findByDateBetween(start, end);
    }

    @Override
    public void approveExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setApproved(true);
        expenseRepository.save(expense);
    }

    @Override
    public void rejectExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        // For rejection, we'll delete the expense instead of marking it as rejected
        // This is a common pattern in expense management systems
        expenseRepository.deleteById(id);
    }
    @Override
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }


    @Override
    public List<Expense> findByAddedBy(User staff) {
        return expenseRepository.findByAddedBy(staff);
    }

	@Override
	public List<Expense> findByAddedByAndDate(User staff, LocalDate now) {
		return expenseRepository.findByAddedByAndDate(staff, now);
	}

    @Override
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    @Override
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }
}
