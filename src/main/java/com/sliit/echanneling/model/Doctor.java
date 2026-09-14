package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor")
@PrimaryKeyJoinColumn(name = "staff_id")
@DiscriminatorValue("DOCTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends Staff {

    private String specialization;
    private String qualification;

    @Column(name = "license_no")
    private String licenseNo;
}
