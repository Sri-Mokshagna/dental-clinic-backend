package com.dentalclinic.DentalClinic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentalclinic.DentalClinic.model.Feedback;
import com.dentalclinic.DentalClinic.model.Patient;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByPatient(Patient patient);
}
