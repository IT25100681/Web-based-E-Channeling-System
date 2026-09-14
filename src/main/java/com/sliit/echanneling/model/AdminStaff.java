package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_staff")
@PrimaryKeyJoinColumn(name = "staff_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStaff extends Staff {

    @Column(name = "admin_level")
    private String adminLevel;
}
