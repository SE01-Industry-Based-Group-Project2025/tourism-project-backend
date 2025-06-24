package com.sl_tourpal.backend.service;


import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.domain.Tour;

public interface TourService {
    Tour createTour(AddTourRequest req);
}
