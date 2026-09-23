package com.sliit.echanneling.features.hospitalmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalForm {

    @NotBlank(message = "Hospital code is required.")
    @Size(max = 50, message = "Hospital code must be 50 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Hospital code can contain only letters, numbers, hyphens, and underscores.")
    private String code;

    @NotBlank(message = "Hospital name is required.")
    @Size(max = 255, message = "Hospital name must be 255 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9 .,'&()/-]+$", message = "Hospital name contains unsupported characters.")
    private String name;

    @Size(max = 50, message = "Contact number must be 50 characters or less.")
    @Pattern(regexp = "^$|^[+]?[0-9 ()-]{7,20}$", message = "Enter a valid contact number.")
    private String contactNo;

    @Size(max = 255, message = "Street must be 255 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'#&()/-]+$", message = "Street contains unsupported characters.")
    private String street;

    @Size(max = 255, message = "City must be 255 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z .'-]+$", message = "City contains unsupported characters.")
    private String city;

    @Size(max = 50, message = "Postal code must be 50 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z0-9 -]+$", message = "Postal code contains unsupported characters.")
    private String postalCode;

    private Boolean active = true;
}
