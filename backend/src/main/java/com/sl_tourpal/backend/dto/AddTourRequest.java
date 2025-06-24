package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddTourRequest {
    // Basic Info
    @NotBlank private String name;
    @NotBlank private String category;
    @NotNull  private Integer durationValue;
    @NotBlank private String durationUnit;
    @Size(max = 500) private String shortDescription;
    @NotEmpty private List<@NotBlank String> highlights;
    @NotBlank private String difficulty;
    @NotBlank private String region;
    private Set<String> activities;

    // Itinerary
    @NotEmpty private List<ItineraryDayDTO> itineraryDays;

    // Accommodation
    @NotEmpty private List<AccommodationDTO> accommodations;

    // Pricing
    @NotEmpty private List<PricingTierDTO> pricingTiers;

    // Availability
    @NotEmpty private List<AvailabilityRangeDTO> availabilityRanges;

    // Media
    @NotEmpty private List<TourImageDTO> images;

    // getters + setters omitted for brevity
}