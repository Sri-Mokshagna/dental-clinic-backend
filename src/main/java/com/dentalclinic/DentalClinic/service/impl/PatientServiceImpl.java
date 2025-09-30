package com.dentalclinic.DentalClinic.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.PatientRepository;
import com.dentalclinic.DentalClinic.repository.UserRepository;
import com.dentalclinic.DentalClinic.service.PatientService;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {
	@Autowired
    private final PatientRepository patientRepository;
	private final UserRepository userRepository;
    public PatientServiceImpl(PatientRepository patientRepository,UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Optional<Patient> findByUsername(String username) {
        // Not supported: patients are not linked to users
        return Optional.empty();
    }

 

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> findByDoctor(User doctor) {
        return patientRepository.findByDoctor(doctor);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }
    public Optional<Patient> findByPhoneNumber(String phone) {
        return patientRepository.findByPhoneNumber(phone);
    }
	@Override
	public void updatePatientInfo(Long id, String medicalInfo, double treatmentAmount) {
		// TODO Auto-generated method stub
		
	}
	public Patient linkOrCreatePatient(User user) {
	    // Legacy no-op: return existing by phone or create without user link
	    Optional<Patient> existingPatient = patientRepository.findByPhoneNumber(user.getPhoneNumber());
	    if (existingPatient.isPresent()) {
	        Patient patient = existingPatient.get();
	        patient.setFullName(user.getFullName());
	        return patientRepository.save(patient);
	    }
	    Patient patient = new Patient();
	    patient.setFullName(user.getFullName());
	    patient.setPhoneNumber(user.getPhoneNumber());
	    return patientRepository.save(patient);
	}
	public Patient addOrLinkPatient(Patient newPatient, Long doctorId) {
        // fetch doctor as user
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // check if patient already exists by phone number
        Optional<Patient> existingOpt = patientRepository.findByPhoneNumber(newPatient.getPhoneNumber());

        if (existingOpt.isPresent()) {
            Patient existingPatient = existingOpt.get();

            // update details if provided
            if (newPatient.getFullName() != null && !newPatient.getFullName().isEmpty()) {
                existingPatient.setFullName(newPatient.getFullName());
            }
            if (newPatient.getMedicalInfo() != null) {
                existingPatient.setMedicalInfo(newPatient.getMedicalInfo());
            }
            

            // link doctor (User with role = DOCTOR)
            existingPatient.setDoctor(doctor);

            return patientRepository.save(existingPatient);
        }

        // no existing patient → create new one
        newPatient.setDoctor(doctor);
        return patientRepository.save(newPatient);
    }
	public Optional<Patient> findByUser(User user) {
	    // Not supported: patients are not linked to users
	    return Optional.empty();
	}

	@Override
	public void deleteById(Long id) {
		patientRepository.deleteById(id);
	}


	
}
