package com.sliit.echanneling.features.hospitalmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecializationForm {

    @NotBlank(message = "Specialization code is required.")
    @Size(max = 50, message = "Specialization code must be 50 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Specialization code can contain only letters, numbers, hyphens, and underscores.")
    private String code;

    @NotBlank(message = "Specialization name is required.")
    @Size(max = 255, message = "Specialization name must be 255 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9 .,'&()/-]+$", message = "Specialization name contains unsupported characters.")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'&()/:;-]+$", message = "Description contains unsupported characters.")
    private String description;

    private Boolean active = true;
}
