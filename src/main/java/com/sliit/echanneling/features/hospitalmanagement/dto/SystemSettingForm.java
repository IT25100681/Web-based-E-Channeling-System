package com.sliit.echanneling.features.hospitalmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemSettingForm {

    @NotBlank(message = "Setting key is required.")
    @Size(max = 255, message = "Setting key must be 255 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Setting key can contain only letters, numbers, dots, hyphens, and underscores.")
    private String settingKey;

    @NotBlank(message = "Setting value is required.")
    @Size(max = 255, message = "Setting value must be 255 characters or less.")
    @Pattern(regexp = "^[A-Za-z0-9 .,:_@/-]+$", message = "Setting value contains unsupported characters.")
    private String settingValue;

    @Size(max = 500, message = "Description must be 500 characters or less.")
    @Pattern(regexp = "^$|^[A-Za-z0-9 .,'&()/:;_-]+$", message = "Description contains unsupported characters.")
    private String description;
}
