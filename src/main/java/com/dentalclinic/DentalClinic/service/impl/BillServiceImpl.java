package com.dentalclinic.DentalClinic.service.impl;

import com.dentalclinic.DentalClinic.model.Bill;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.repository.BillRepository;
import com.dentalclinic.DentalClinic.service.BillService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public Bill save(Bill bill) {
        if (bill.getIssuedAt() == null) {
            bill.setIssuedAt(LocalDate.now());
        }
        return billRepository.save(bill);
    }

    @Override
    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    @Override
    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    @Override
    public List<Bill> findByPatient(Patient patient) {
        return billRepository.findByPatient(patient);
    }

    @Override
    public void deleteById(Long id) {
        billRepository.deleteById(id);
    }
}


