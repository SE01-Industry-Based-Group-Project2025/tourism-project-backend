package com.sl_tourpal.backend.service;

import java.util.List;
import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.dto.TouristTourRequestDTO;
import com.sl_tourpal.backend.dto.TourResponseDTO;

public interface TourService {
    
    // Existing methods
    Tour createTour(AddTourRequest req);
    List<Tour> getAllTours();
    Tour getTourById(Long id);
    Tour updateTour(Long id, AddTourRequest req);
    void deleteTour(Long id);
    
    // New custom tour methods
    Tour createCustomTourRequest(TouristTourRequestDTO touristRequest, String userEmail);
    List<Tour> getCustomToursByUser(String userEmail);
    List<TourResponseDTO> getPendingCustomTours();
    List<TourResponseDTO> getAllCustomTours();
    List<Tour> getAllCustomToursAsEntity(); // For filtering endpoints
    Tour approveCustomTour(Long tourId);
    Tour rejectCustomTour(Long tourId, String reason);
    Tour updateCustomTourStatus(Long tourId, String status);
    
    // Additional filtering methods
    List<Tour> findByIsCustomTrueAndStatus(String status);
    List<Tour> findByIsCustomFalse();
    
    // Template/Tour filtering methods
    List<Tour> getToursByTemplate(boolean isTemplate);
}
