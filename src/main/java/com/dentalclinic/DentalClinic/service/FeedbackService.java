package com.dentalclinic.DentalClinic.service;


import java.util.List;

import com.dentalclinic.DentalClinic.model.Feedback;
import com.dentalclinic.DentalClinic.model.Patient;

public interface FeedbackService {
    Feedback save(Feedback feedback);
    List<Feedback> findByPatient(Patient patient);
}
