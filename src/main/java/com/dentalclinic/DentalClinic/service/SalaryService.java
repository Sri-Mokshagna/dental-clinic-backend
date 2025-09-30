package com.dentalclinic.DentalClinic.service;

import com.dentalclinic.DentalClinic.model.Salary;
import com.dentalclinic.DentalClinic.model.User;

import java.time.LocalDate;
import java.util.List;

public interface SalaryService {

    Salary save(Salary salary);

    List<Salary> findByUser(User user);

    List<Salary> findByDateRange(LocalDate start, LocalDate end);

    List<Salary> findAll();

    void addSalary(Long staffId, double amount);
}

