package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDTO {
    @NotBlank private String title;
    private String description;
    @NotEmpty private List<@NotBlank String> images;
    // getters / setters
}