package com.dentalclinic.DentalClinic.service;


import java.util.List;
import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;

public interface UserService {
    User save(User user);
    public Optional<User> findByUsernameOrPhone(String identifier);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findAll();
    void deleteById(Long id);
    void assignPatientToDoctor(User patient, User doctor);
    void updatePatientInfo(Long id, String medicalInfo, double treatmentAmount);
	List<User> findDoctors();
	Optional<User> findById(Long id);
	boolean phoneExists(String phoneNumber);
	Optional<User> findByPhoneNumber(String phoneNumber);
}
