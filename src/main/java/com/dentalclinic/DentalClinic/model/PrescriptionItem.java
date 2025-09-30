package com.dentalclinic.DentalClinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_note_id", nullable = false)
    private PrescriptionNote prescriptionNote;
    
    @Column(name = "medication_name", nullable = false)
    private String medicationName;
    
    @Column(name = "dosage")
    private String dosage;
    
    @Column(name = "frequency")
    private String frequency;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "unit_price")
    private Double unitPrice;
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (totalPrice == null && unitPrice != null && quantity != null) {
            totalPrice = unitPrice * quantity;
        }
    }
}
