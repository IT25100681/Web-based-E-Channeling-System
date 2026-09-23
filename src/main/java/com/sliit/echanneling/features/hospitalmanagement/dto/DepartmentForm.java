package com.sliit.echanneling.features.hospitalmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentForm {

    @NotBlank(message = "Department code is required.")
    @Size(max = 50, message = "Department code must be 50 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Department code can contain only letters, numbers, hyphens, and underscores.")
    private String code;

    @NotBlank(message = "Department name is required.")
    @Size(max = 255, message = "Department name must be 255 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9 .,'&()/-]+$", message = "Department name contains unsupported characters.")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'&()/:;-]+$", message = "Description contains unsupported characters.")
    private String description;

    @NotNull(message = "Hospital is required.")
    private Long hospitalId;

    private Boolean active = true;
}
