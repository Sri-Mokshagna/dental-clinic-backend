package com.dentalclinic.DentalClinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String fromPhone;

    @Column(length = 100)
    private String fromName;

    @Column(length = 2000)
    private String message;

    private LocalDateTime receivedAt;
}


