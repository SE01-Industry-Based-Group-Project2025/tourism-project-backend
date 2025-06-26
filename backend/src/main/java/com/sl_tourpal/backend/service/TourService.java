package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.domain.Tour;

import java.util.List;

public interface TourService {
    Tour createTour(AddTourRequest req);
    List<Tour> getAllTours();
    Tour getTourById(Long id);
    Tour updateTour(Long id, AddTourRequest req);
    void deleteTour(Long id);
}
