package com.dentalclinic.DentalClinic.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.Role;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.AppointmentRepository;
import com.dentalclinic.DentalClinic.repository.PatientRepository;
import com.dentalclinic.DentalClinic.repository.UserRepository;
import com.dentalclinic.DentalClinic.service.UserService;
import com.dentalclinic.DentalClinic.service.PatientService;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final AppointmentRepository appointmentRepository;
    
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PatientRepository patientRepository,
                           PatientService patientService,
                           AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public User save(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.PATIENT); // default role
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }



    @Override
    public Optional<User> findByUsername(String username) {
    	
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        // Check if user has associated appointments before deletion
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Appointment> doctorAppointments = appointmentRepository.findByDoctor(user);
            List<Appointment> staffAppointments = appointmentRepository.findByStaff(user);
            
            if (!doctorAppointments.isEmpty() || !staffAppointments.isEmpty()) {
                throw new RuntimeException("Cannot delete user. User has associated appointments. Please reassign or delete appointments first.");
            }
        }
        userRepository.deleteById(id);
    }
    @Override
    public void assignPatientToDoctor(User patient, User doctor) {
        patient.setRole(Role.PATIENT);
        // maybe set doctor_id column (add in User entity if needed)
        userRepository.save(patient);
    }

    @Override
    public void updatePatientInfo(Long id, String medicalInfo, double treatmentAmount) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.setMedicalInfo(medicalInfo);
        patient.setTreatmentAmount(treatmentAmount);
        patientRepository.save(patient);
    }

	@Override
	public List<User> findDoctors() {
		// TODO Auto-generated method stub
		return userRepository.findByRole(Role.DOCTOR);
	}

	@Override
	public Optional<User> findById(Long id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id);
	}
	public User registerPatient(User user) {
	    // Save user
	    user.setRole(Role.PATIENT);
	    User savedUser = userRepository.save(user);

	    // Link or create patient record
	    patientService.linkOrCreatePatient(savedUser);

	    return savedUser;
	}
	public Optional<User> findByUsernameOrPhone(String identifier) {
	    return userRepository.findByUsername(identifier)
	            .or(() -> userRepository.findByPhoneNumber(identifier));
	}
	public boolean phoneExists(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

	@Override
	public Optional<User> findByPhoneNumber(String phoneNumber) {
		return userRepository.findByPhoneNumber(phoneNumber);
	}
	


}
