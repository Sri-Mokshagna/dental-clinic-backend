package com.dentalclinic.DentalClinic.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinic_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // General settings
    private double defaultConsultationFee;

    // Appointment timings
    @Column(length = 10)
    private String startTime; // e.g. 10:00

    @Column(length = 10)
    private String endTime; // e.g. 20:00

    private int slotDuration; // minutes

    // Clinic profile
    @Column(length = 200)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 200)
    private String contact;
}


