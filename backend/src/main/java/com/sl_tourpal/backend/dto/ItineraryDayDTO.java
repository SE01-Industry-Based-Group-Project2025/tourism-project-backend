package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItineraryDayDTO {
    @NotNull private Integer dayNumber;
    @NotBlank private String title;
    @NotBlank private String description;
    @NotBlank private String imageUrl;
    private Set<String> destinations;
    // getters / setters
}