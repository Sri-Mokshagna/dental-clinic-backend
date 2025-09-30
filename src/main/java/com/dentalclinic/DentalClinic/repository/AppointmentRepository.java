package com.dentalclinic.DentalClinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentalclinic.DentalClinic.model.Appointment;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(User doctor);

    List<Appointment> findByStaff(User staff);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);

	List<Appointment> findByDoctorAndAppointmentDateBetween(User staff,LocalDateTime start,LocalDateTime end);
	
	List<Appointment> findByStaffAndAppointmentDateBetween(User staff,LocalDateTime start,LocalDateTime end);
}
