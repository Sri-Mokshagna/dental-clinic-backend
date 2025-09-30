package com.dentalclinic.DentalClinic.service;
import java.util.List;
import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;

public interface PatientService {
    Patient save(Patient patient);
    List<Patient> findByDoctor(User doctor);
    List<Patient> findAll();
    Optional<Patient> findById(Long id);
	Optional<Patient> findByUsername(String username);
	Optional<Patient> findByPhoneNumber(String phoneNumber);
	void updatePatientInfo(Long id, String medicalInfo, double treatmentAmount);
	public Patient linkOrCreatePatient(User user);
	public Patient addOrLinkPatient(Patient newPatient, Long doctorId);
	Optional<Patient> findByUser(User user);
    void deleteById(Long id);
}
