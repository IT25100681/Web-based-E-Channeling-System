package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacist")
@PrimaryKeyJoinColumn(name = "staff_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacist extends Staff {

    private String qualification;
}
