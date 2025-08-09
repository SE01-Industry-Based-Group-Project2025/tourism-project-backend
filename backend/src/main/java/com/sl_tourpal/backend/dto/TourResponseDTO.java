package com.sl_tourpal.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {
    private Long id;
    private String name;
    private String category;
    private Integer durationValue;
    private String durationUnit;
    private String shortDescription;
    private List<String> highlights;
    private String difficulty;
    private String region;
    private Set<String> activities;
    
    // Tour management fields
    private String status;
    private Boolean isCustom;
    private boolean isTemplate;
    private Integer availableSpots;
    private BigDecimal price;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Simplified user info instead of full User object
    private UserSummaryDTO createdBy;
    
    // Related entities
    private List<AccommodationDTO> accommodations;
    private List<ItineraryDayDTO> itinerary;
    
    // Custom setter for primitive boolean isTemplate to ensure proper mapping
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
    
    @JsonProperty("isTemplate")
    public boolean isTemplate() {
        return isTemplate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
        
        public UserSummaryDTO(Long id, String email, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = firstName + " " + lastName;
        }
    }
}
