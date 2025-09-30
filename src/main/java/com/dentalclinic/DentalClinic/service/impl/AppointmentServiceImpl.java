package com.dentalclinic.DentalClinic.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;
import com.dentalclinic.DentalClinic.repository.AppointmentRepository;
import com.dentalclinic.DentalClinic.service.AppointmentService;
import com.dentalclinic.DentalClinic.util.WhatsAppSender;
import com.dentalclinic.DentalClinic.service.AppointmentNotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    
    @Autowired
    private AppointmentNotificationService appointmentNotificationService;
    private final WhatsAppSender whatsAppSender;
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,WhatsAppSender whatsAppSender) {
        this.appointmentRepository = appointmentRepository;
        this.whatsAppSender = whatsAppSender;   
    }

    @Override
    public Appointment save(Appointment appointment) {
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        Patient patient = savedAppointment.getPatient();
        if (patient != null && patient.getPhoneNumber() != null) {
            String patientName = patient.getFullName() != null ? patient.getFullName() : "Patient";
            String status = savedAppointment.getStatus() != null ? savedAppointment.getStatus() : "Scheduled";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

            String dateTime = savedAppointment.getAppointmentDate().format(formatter); // format nicely if needed

            whatsAppSender.sendAppointmentUpdate(patient.getPhoneNumber(), patientName, status, dateTime);
        }
        
        return savedAppointment;
    }
    

    @Override
    public List<Appointment> findByPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public List<Appointment> findByDoctor(User doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    @Override
    public List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateBetween(start, end);
    }

	@Override
	public List<Appointment> findAll() {
		// TODO Auto-generated method stub
		return appointmentRepository.findAll();
	}

	@Override
	public List<Appointment> findAppointmentsForToday() {
		return appointmentRepository.findByAppointmentDateBetween(LocalDateTime.now(), LocalDateTime.now());
	}
	public List<Appointment> findAppointmentsForDoctorToday(User doctor) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return appointmentRepository.findByDoctorAndAppointmentDateBetween(doctor, startOfDay,endOfDay);
    }

	@Override
	public List<Appointment> findAppointmentsForStaffToday(User staff) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
		return appointmentRepository.findByStaffAndAppointmentDateBetween(staff, startOfDay,endOfDay);
	}
	
	@Override
	public Optional<Appointment> findById(Long id) {
		return appointmentRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		appointmentRepository.deleteById(id);
	}
}
