package com.sliit.echanneling.model;

import com.sliit.echanneling.model.embedded.Address;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long hospitalId;

    @Column(nullable = false)
    private String name;

    @Column(name = "hospital_code")
    private String code;

    @Embedded
    private Address address;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "active", columnDefinition = "bit default 1")
    @Builder.Default
    private Boolean active = true;
}
