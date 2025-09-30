package com.dentalclinic.DentalClinic.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.User;

public interface ExpenseService {
    Expense save(Expense expense);
    List<Expense> findByUser(User user);
    List<Expense> findPendingApproval();
    List<Expense> findByDateRange(LocalDate start, LocalDate end);
    void approveExpense(Long id);
    void rejectExpense(Long id);
	List<Expense> findByAddedBy(User staff);
	void deleteById(Long id);
	List<Expense> findByAddedByAndDate(User staff, LocalDate now);
    List<Expense> findAll();
    Optional<Expense> findById(Long id);
}
