package com.dentalclinic.DentalClinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentalclinic.DentalClinic.model.Expense;
import com.dentalclinic.DentalClinic.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByAddedBy(User user);

    List<Expense> findByApproved(boolean approved);

    List<Expense> findByDateBetween(LocalDate start, LocalDate end);

	List<Expense> findByAddedByAndDate(User staff, LocalDate now);
}
