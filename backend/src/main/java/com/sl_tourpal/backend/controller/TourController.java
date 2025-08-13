package com.sl_tourpal.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.dto.TourResponseDTO;
import com.sl_tourpal.backend.dto.TouristTourRequestDTO;
import com.sl_tourpal.backend.security.JwtUtil;
import com.sl_tourpal.backend.service.TourService;
import com.sl_tourpal.backend.util.TourMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService; // injected service for tour operations
    private final JwtUtil jwtUtil;
    private final TourMapper tourMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponseDTO> createTour(@Valid @RequestBody AddTourRequest tourRequest) {
        // Validate required fields if it's not a template
        if (!tourRequest.isTemplate()) {
            // Additional validation for real tours will be handled in service layer
        }
        Tour savedTour = tourService.createTour(tourRequest);
        TourResponseDTO response = tourMapper.toResponseDTO(savedTour);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TourResponseDTO>> getAllTours(
            @RequestParam(required = false) Boolean isTemplate) {
        List<Tour> tours;
        if (isTemplate != null) {
            tours = tourService.getToursByTemplate(isTemplate);
        } else {
            tours = tourService.getAllTours();
        }
        List<TourResponseDTO> response = tours.stream()
                .map(tourMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable Long id) {
        Tour tour = tourService.getTourById(id);
        TourResponseDTO response = tourMapper.toResponseDTO(tour);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponseDTO> updateTour(@PathVariable Long id, @Valid @RequestBody AddTourRequest req) {
        Tour updated = tourService.updateTour(id, req);
        TourResponseDTO response = tourMapper.toResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    // Tourists can create custom tour requests
    @PostMapping("/custom-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createCustomTourRequest(
            @Valid @RequestBody TouristTourRequestDTO touristRequest,
            @RequestHeader("Authorization") String authHeader) {

        try {
            System.out.println("=== CUSTOM TOUR REQUEST DEBUG ===");
            System.out.println("Received request: " + touristRequest);
            System.out.println("Name: " + touristRequest.getName());
            System.out
                    .println("Duration: " + touristRequest.getDurationValue() + " " + touristRequest.getDurationUnit());
            System.out.println("Region: " + touristRequest.getRegion());
            System.out.println("Activities: " + touristRequest.getActivities());
            System.out.println("Price: " + touristRequest.getPrice());
            System.out.println("Group Size: " + touristRequest.getGroupSize());
            System.out.println("Start Date: " + touristRequest.getStartDate());
            System.out.println("End Date: " + touristRequest.getEndDate());
            System.out.println("Special Requirements: " + touristRequest.getSpecialRequirements());

            String token = authHeader.substring(7); // Remove "Bearer "
            String userEmail = jwtUtil.extractUsername(token);
            System.out.println("User email: " + userEmail);

            Tour customTour = tourService.createCustomTourRequest(touristRequest, userEmail);
            System.out.println("Tour created successfully: " + customTour.getId());
            TourResponseDTO response = tourMapper.toResponseDTO(customTour);
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            System.err.println("Error creating custom tour: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Tourists can view their own custom tour requests
    @GetMapping("/my-custom-tours")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TourResponseDTO>> getMyCustomTours(
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7);
            String userEmail = jwtUtil.extractUsername(token);

            List<Tour> customTours = tourService.getCustomToursByUser(userEmail);
            List<TourResponseDTO> tourDTOs = customTours.stream()
                    .map(tourMapper::toResponseDTO)
                    .toList();
            return ResponseEntity.ok(tourDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin can view all custom tours
    @GetMapping("/all-custom")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TourResponseDTO>> getAllCustomTours() {
        List<TourResponseDTO> customTours = tourService.getAllCustomTours();
        return ResponseEntity.ok(customTours);
    }

    // Admin can view all pending custom tours
    @GetMapping("/pending-custom")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TourResponseDTO>> getPendingCustomTours() {
        List<TourResponseDTO> pendingTours = tourService.getPendingCustomTours();
        return ResponseEntity.ok(pendingTours);
    }

    // Admin can approve custom tours
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponseDTO> approveCustomTour(@PathVariable Long id) {
        try {
            Tour approvedTour = tourService.approveCustomTour(id);
            TourResponseDTO response = tourMapper.toResponseDTO(approvedTour);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin can reject custom tours
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponseDTO> rejectCustomTour(
            @PathVariable Long id,
            @RequestParam String reason) {
        try {
            Tour rejectedTour = tourService.rejectCustomTour(id, reason);
            TourResponseDTO response = tourMapper.toResponseDTO(rejectedTour);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin can update custom tour status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TourResponseDTO> updateCustomTourStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Tour updatedTour = tourService.updateCustomTourStatus(id, status);
            TourResponseDTO response = tourMapper.toResponseDTO(updatedTour);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all tours with filtering
    @GetMapping("/filter")
    public ResponseEntity<List<TourResponseDTO>> getToursWithFilter(
            @RequestParam(required = false) Boolean isCustom,
            @RequestParam(required = false) String status) {

        List<Tour> tours;

        if (isCustom != null && isCustom) {
            if (status != null) {
                tours = tourService.findByIsCustomTrueAndStatus(status);
            } else {
                tours = tourService.getAllCustomToursAsEntity();
            }
        } else if (isCustom != null && !isCustom) {
            tours = tourService.findByIsCustomFalse();
        } else {
            tours = tourService.getAllTours();
        }

        // Convert to DTOs before returning
        List<TourResponseDTO> response = tours.stream()
                .map(tourMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }
}
