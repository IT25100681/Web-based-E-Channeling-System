package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "receptionist")
@PrimaryKeyJoinColumn(name = "staff_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receptionist extends Staff {

    @Column(name = "desk_no")
    private String deskNo;
}
