package com.dentalclinic.DentalClinic.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;

public interface AppointmentService {
    Appointment save(Appointment appointment);
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Appointment> findAll();
	List<Appointment> findAppointmentsForToday();
	public List<Appointment> findAppointmentsForDoctorToday(User doctor);
    public List<Appointment> findAppointmentsForStaffToday(User staff);
    Optional<Appointment> findById(Long id);
    void deleteById(Long id);
}
