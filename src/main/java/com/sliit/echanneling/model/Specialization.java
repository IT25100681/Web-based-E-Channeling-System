package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specialization_id")
    private Long specializationId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
