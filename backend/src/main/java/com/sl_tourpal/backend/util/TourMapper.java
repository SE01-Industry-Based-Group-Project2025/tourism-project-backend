package com.sl_tourpal.backend.util;

import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.dto.TourResponseDTO;
import com.sl_tourpal.backend.dto.AccommodationDTO;
import com.sl_tourpal.backend.dto.ItineraryDayDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TourMapper {

    /**
     * Convert a Tour entity to TourResponseDTO
     */
    public TourResponseDTO toResponseDTO(Tour tour) {
        if (tour == null) {
            return null;
        }

        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setCategory(tour.getCategory());
        dto.setDurationValue(tour.getDurationValue());
        dto.setDurationUnit(tour.getDurationUnit());
        dto.setShortDescription(tour.getShortDescription());
        dto.setHighlights(tour.getHighlights());
        dto.setDifficulty(tour.getDifficulty());
        dto.setRegion(tour.getRegion());
        dto.setActivities(tour.getActivities());
        dto.setStatus(tour.getStatus());
        dto.setIsCustom(tour.getIsCustom());
        dto.setIsTemplate(tour.getIsTemplate()); // Ensure correct mapping of isTemplate field
        dto.setAvailableSpots(tour.getAvailableSpots());
        dto.setPrice(tour.getPrice());
        dto.setCreatedAt(tour.getCreatedAt());
        dto.setUpdatedAt(tour.getUpdatedAt());

        // Map the createdBy user safely
        if (tour.getCreatedBy() != null) {
            dto.setCreatedBy(mapUserToSummary(tour.getCreatedBy()));
        }

        // Map accommodations
        if (tour.getAccommodations() != null) {
            dto.setAccommodations(tour.getAccommodations().stream()
                .map(accommodation -> {
                    AccommodationDTO accDto = new AccommodationDTO();
                    accDto.setTitle(accommodation.getTitle());
                    accDto.setDescription(accommodation.getDescription());
                    // Note: images not mapped as it's a complex nested structure
                    return accDto;
                }).collect(Collectors.toList()));
        }

        // Map itinerary days
        if (tour.getItineraryDays() != null) {
            dto.setItinerary(tour.getItineraryDays().stream()
                .map(day -> {
                    ItineraryDayDTO dayDto = new ItineraryDayDTO();
                    dayDto.setDayNumber(day.getDayNumber());
                    dayDto.setTitle(day.getTitle());
                    dayDto.setDescription(day.getDescription());
                    dayDto.setImageUrl(day.getImageUrl());
                    dayDto.setDestinations(day.getDestinations());
                    return dayDto;
                }).collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Convert a list of Tour entities to TourResponseDTO list
     */
    public List<TourResponseDTO> toResponseDTOList(List<Tour> tours) {
        return tours.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert User entity to UserSummaryDTO
     */
    private TourResponseDTO.UserSummaryDTO mapUserToSummary(User user) {
        if (user == null) {
            return null;
        }

        return new TourResponseDTO.UserSummaryDTO(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }
}
