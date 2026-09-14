package com.sliit.echanneling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintRequestDTO {
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;
}
