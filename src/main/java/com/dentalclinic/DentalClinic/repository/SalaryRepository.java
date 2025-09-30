package com.dentalclinic.DentalClinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentalclinic.DentalClinic.model.Salary;
import com.dentalclinic.DentalClinic.model.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    List<Salary> findByUser(User user);

    List<Salary> findByPaymentDateBetween(LocalDate start, LocalDate end);
}
