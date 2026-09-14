package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medication")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long medicationId;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Column(name = "dosage_form")
    private String dosageForm;

    private String unit;
}
