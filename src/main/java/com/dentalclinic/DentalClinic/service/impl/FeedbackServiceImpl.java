package com.dentalclinic.DentalClinic.service.impl;


import org.springframework.stereotype.Service;

import com.dentalclinic.DentalClinic.model.Feedback;
import com.dentalclinic.DentalClinic.model.Patient;
import com.dentalclinic.DentalClinic.repository.FeedbackRepository;
import com.dentalclinic.DentalClinic.service.FeedbackService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public Feedback save(Feedback feedback) {
        feedback.setDate(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> findByPatient(Patient patient) {
        return feedbackRepository.findByPatient(patient);
    }
}
