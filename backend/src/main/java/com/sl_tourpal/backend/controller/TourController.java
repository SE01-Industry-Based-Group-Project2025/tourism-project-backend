package com.sl_tourpal.backend.controller;

import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.service.TourService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tours")
public class TourController {
    private final TourService tourService;
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tour> createNewTour(@Valid @RequestBody AddTourRequest req) {
        Tour created = tourService.createTour(req);
        return ResponseEntity.status(201).body(created);
    }
}
