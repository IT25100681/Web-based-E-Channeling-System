package com.sliit.echanneling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "complaint_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
