package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Salary;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.SalaryRepository;
import com.dentalclinic.DentalClinic.repository.UserRepository;
import com.dentalclinic.DentalClinic.service.SalaryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;

    public SalaryServiceImpl(SalaryRepository salaryRepository, UserRepository userRepository) {
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Salary save(Salary salary) {
        return salaryRepository.save(salary);
    }

    @Override
    public List<Salary> findByUser(User user) {
        return salaryRepository.findByUser(user);
    }

    @Override
    public List<Salary> findByDateRange(LocalDate start, LocalDate end) {
        return salaryRepository.findByPaymentDateBetween(start, end);
    }

    @Override
    public List<Salary> findAll() {
        return salaryRepository.findAll();
    }

    @Override
    public void addSalary(Long staffId, double amount) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Salary salary = new Salary();
        salary.setUser(staff);
        salary.setAmount(amount);
        salary.setPaymentDate(LocalDate.now());

        salaryRepository.save(salary);
    }
}
