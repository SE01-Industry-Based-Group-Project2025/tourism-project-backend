package com.sl_tourpal.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sl_tourpal.backend.domain.Accommodation;
import com.sl_tourpal.backend.domain.AvailabilityRange;
import com.sl_tourpal.backend.domain.ItineraryDay;
import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.domain.TourImage;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.dto.AddTourRequest;
import com.sl_tourpal.backend.dto.TouristTourRequestDTO; // Fixed: Added missing import
import com.sl_tourpal.backend.repository.TourRepository;
import com.sl_tourpal.backend.repository.UserRepository;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepo;
    
    public TourServiceImpl(TourRepository tourRepo) {
        this.tourRepo = tourRepo;
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Tour createTour(AddTourRequest req) {
        Tour tour = new Tour();
        // map basic fields
        tour.setName(req.getName());
        tour.setCategory(req.getCategory());
        tour.setDurationValue(req.getDurationValue());
        tour.setDurationUnit(req.getDurationUnit());
        tour.setShortDescription(req.getShortDescription());
        tour.setHighlights(req.getHighlights());
        tour.setDifficulty(req.getDifficulty());
        tour.setRegion(req.getRegion());
        tour.setActivities(req.getActivities());

        // Map new fields with defaults if not provided
        tour.setStatus(req.getStatus() != null ? req.getStatus() : "Incomplete");
        tour.setIsCustom(Boolean.TRUE.equals(req.getIsCustom()));
        if (req.getAvailableSpots() != null) {
            tour.setAvailableSpots(req.getAvailableSpots());
        } else {
            tour.setAvailableSpots(Integer.valueOf(0));
        }
        tour.setPrice(req.getPrice());

        // map itinerary
        if (req.getItineraryDays() != null) {
            tour.setItineraryDays(
                req.getItineraryDays().stream().map(dto -> {
                    ItineraryDay d = new ItineraryDay();
                    d.setDayNumber(dto.getDayNumber());
                    d.setTitle(dto.getTitle());
                    d.setDescription(dto.getDescription());
                    d.setImageUrl(dto.getImageUrl());
                    d.setDestinations(dto.getDestinations());
                    d.setTour(tour);
                    return d;
                }).collect(Collectors.toList())
            );
        } else {
            tour.setItineraryDays(new ArrayList<>());
        }

        // map accommodations
        if (req.getAccommodations() != null) {
            tour.setAccommodations(
                req.getAccommodations().stream().map(dto -> {
                    Accommodation a = new Accommodation();
                    a.setTitle(dto.getTitle());
                    a.setDescription(dto.getDescription());
                    // Convert AccommodationImageDto to String URLs/base64
                    if (dto.getImages() != null) {
                        List<String> imageUrls = dto.getImages().stream()
                            .map(img -> img.getPreview()) // Use preview field as the image URL/base64
                            .filter(url -> url != null && !url.isEmpty())
                            .collect(Collectors.toList());
                        a.setImages(imageUrls);
                    }
                    a.setTour(tour);
                    return a;
                }).collect(Collectors.toList())
            );
        } else {
            tour.setAccommodations(new ArrayList<>());
        }

        // map availability
        if (req.getAvailabilityRanges() != null) {
            tour.setAvailabilityRanges(
                req.getAvailabilityRanges().stream().map(dto -> {
                    AvailabilityRange ar = new AvailabilityRange();
                    ar.setStartDate(dto.getStartDate());
                    ar.setEndDate(dto.getEndDate());
                    ar.setTour(tour);
                    return ar;
                }).collect(Collectors.toList())
            );
        } else {
            tour.setAvailabilityRanges(new ArrayList<>());
        }

        // map images (optional)
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            tour.setImages(
                req.getImages().stream().map(dto -> {
                    TourImage img = new TourImage();
                    img.setUrl(dto.getUrl());
                    img.setPrimary(dto.isPrimary());
                    img.setTour(tour);
                    return img;
                }).collect(Collectors.toList())
            );
        } else {
            tour.setImages(new ArrayList<>());
        }

        // saves tour + cascades
        return tourRepo.save(tour);
    }

    @Override
    public List<Tour> getAllTours() {
        return tourRepo.findAll();
    }

    @Override
    public Tour getTourById(Long id) {
        return tourRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tour not found with id: " + id));
    }

    @Override
    @Transactional
    public Tour updateTour(Long id, AddTourRequest req) {
        Tour existingTour = getTourById(id);
        
        // Update basic fields
        existingTour.setName(req.getName());
        existingTour.setCategory(req.getCategory());
        existingTour.setDurationValue(req.getDurationValue());
        existingTour.setDurationUnit(req.getDurationUnit());
        existingTour.setShortDescription(req.getShortDescription());
        existingTour.setHighlights(req.getHighlights());
        existingTour.setDifficulty(req.getDifficulty());
        existingTour.setRegion(req.getRegion());
        existingTour.setActivities(req.getActivities());
        
        // Update new fields
        existingTour.setStatus(req.getStatus() != null ? req.getStatus() : "Incomplete");
        existingTour.setIsCustom(Boolean.TRUE.equals(req.getIsCustom()));
        if (req.getAvailableSpots() != null) {
            existingTour.setAvailableSpots(req.getAvailableSpots());
        } else {
            existingTour.setAvailableSpots(Integer.valueOf(0));
        }
        existingTour.setPrice(req.getPrice());

        // Update itinerary days - clear existing and add new ones
        existingTour.getItineraryDays().clear();
        if (req.getItineraryDays() != null) {
            List<ItineraryDay> newItineraryDays = req.getItineraryDays().stream().map(dto -> {
                ItineraryDay d = new ItineraryDay();
                d.setDayNumber(dto.getDayNumber());
                d.setTitle(dto.getTitle());
                d.setDescription(dto.getDescription());
                d.setImageUrl(dto.getImageUrl());
                d.setDestinations(dto.getDestinations());
                d.setTour(existingTour);
                return d;
            }).collect(Collectors.toList());
            existingTour.getItineraryDays().addAll(newItineraryDays);
        }

        // Update accommodations - clear existing and add new ones
        existingTour.getAccommodations().clear();
        if (req.getAccommodations() != null) {
            List<Accommodation> newAccommodations = req.getAccommodations().stream().map(dto -> {
                Accommodation a = new Accommodation();
                a.setTitle(dto.getTitle());
                a.setDescription(dto.getDescription());
                // Convert AccommodationImageDto to String URLs/base64
                if (dto.getImages() != null) {
                    List<String> imageUrls = dto.getImages().stream()
                        .map(img -> img.getPreview())
                        .filter(url -> url != null && !url.isEmpty())
                        .collect(Collectors.toList());
                    a.setImages(imageUrls);
                }
                a.setTour(existingTour);
                return a;
            }).collect(Collectors.toList());
            existingTour.getAccommodations().addAll(newAccommodations);
        }

        // Update availability ranges - clear existing and add new ones
        existingTour.getAvailabilityRanges().clear();
        if (req.getAvailabilityRanges() != null) {
            List<AvailabilityRange> newAvailabilityRanges = req.getAvailabilityRanges().stream().map(dto -> {
                AvailabilityRange ar = new AvailabilityRange();
                ar.setStartDate(dto.getStartDate());
                ar.setEndDate(dto.getEndDate());
                ar.setTour(existingTour);
                return ar;
            }).collect(Collectors.toList());
            existingTour.getAvailabilityRanges().addAll(newAvailabilityRanges);
        }

        // Update images - clear existing and add new ones
        existingTour.getImages().clear();
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<TourImage> newImages = req.getImages().stream().map(dto -> {
                TourImage img = new TourImage();
                img.setUrl(dto.getUrl());
                img.setPrimary(dto.isPrimary());
                img.setTour(existingTour);
                return img;
            }).collect(Collectors.toList());
            existingTour.getImages().addAll(newImages);
        }

        return tourRepo.save(existingTour);
    }

    @Override
    @Transactional
    public void deleteTour(Long id) {
        Tour tour = getTourById(id);
        tourRepo.delete(tour);
    }

    @Override // Fixed: Added @Override annotation
    public Tour createCustomTourRequest(TouristTourRequestDTO touristRequest, String userEmail) {
        // Get the requesting user
        User requestingUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate date range
        if (touristRequest.getEndDate().isBefore(touristRequest.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        // Convert TouristTourRequestDTO to AddTourRequest
        AddTourRequest addTourRequest = new AddTourRequest();
        addTourRequest.setName(touristRequest.getName());
        addTourRequest.setCategory("Custom Tour Request");
        addTourRequest.setDurationValue(touristRequest.getDurationValue());
        addTourRequest.setDurationUnit(touristRequest.getDurationUnit());
        addTourRequest.setRegion(touristRequest.getRegion());
        addTourRequest.setActivities(new HashSet<>(touristRequest.getActivities()));
        addTourRequest.setPrice(touristRequest.getPrice());
        addTourRequest.setShortDescription(touristRequest.getSpecialRequirements());
        addTourRequest.setAvailableSpots(touristRequest.getGroupSize());
        addTourRequest.setDifficulty("To be determined");
        addTourRequest.setIsCustom(true);
        addTourRequest.setStatus("PENDING_APPROVAL");
        
        // Set highlights with user and date info
        List<String> highlights = new ArrayList<>();
        highlights.add("Custom tour request");
        highlights.add("Requested by: " + requestingUser.getFirstName() + " " + requestingUser.getLastName());
        highlights.add("Contact: " + requestingUser.getEmail());
        highlights.add("Requested dates: " + touristRequest.getStartDate() + " to " + touristRequest.getEndDate());
        highlights.add("Group size: " + touristRequest.getGroupSize() + " people");
        addTourRequest.setHighlights(highlights);
        
        // Create the tour using existing method
        Tour customTour = createTour(addTourRequest);
        
        // Set the creating user and timestamps
        customTour.setCreatedBy(requestingUser);
        customTour.setCreatedAt(LocalDateTime.now());
        
        return tourRepo.save(customTour);
    }

    @Override // Fixed: Added @Override annotation
    public List<Tour> getCustomToursByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return tourRepo.findByIsCustomTrueAndCreatedByOrderByCreatedAtDesc(user);
    }

    @Override // Fixed: Added @Override annotation
    public List<Tour> getPendingCustomTours() {
        return tourRepo.findByIsCustomTrueAndStatus("PENDING_APPROVAL");
    }

    @Override // Fixed: Added @Override annotation
    public List<Tour> getAllCustomTours() {
        return tourRepo.findByIsCustomTrue();
    }

    @Override // Fixed: Added @Override annotation
    public Tour approveCustomTour(Long tourId) {
        Tour tour = tourRepo.findById(tourId)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        
        if (!tour.getIsCustom()) {
            throw new RuntimeException("Only custom tours can be approved");
        }
        
        tour.setStatus("APPROVED");
        tour.setUpdatedAt(LocalDateTime.now());
        return tourRepo.save(tour);
    }

    @Override // Fixed: Added @Override annotation
    public Tour rejectCustomTour(Long tourId, String reason) {
        Tour tour = tourRepo.findById(tourId)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        
        if (!tour.getIsCustom()) {
            throw new RuntimeException("Only custom tours can be rejected");
        }
        
        tour.setStatus("REJECTED");
        tour.setUpdatedAt(LocalDateTime.now());
        
        // Add rejection reason to description
        String currentDesc = tour.getShortDescription() != null ? tour.getShortDescription() : "";
        tour.setShortDescription(currentDesc + "\n\nRejection reason: " + reason);
        
        return tourRepo.save(tour);
    }

    @Override // Fixed: Added @Override annotation
    public Tour updateCustomTourStatus(Long tourId, String status) {
        Tour tour = tourRepo.findById(tourId)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        
        if (!tour.getIsCustom()) {
            throw new RuntimeException("Only custom tours can have status updated");
        }
        
        tour.setStatus(status);
        tour.setUpdatedAt(LocalDateTime.now());
        return tourRepo.save(tour);
    }

    @Override
    public List<Tour> findByIsCustomTrueAndStatus(String status) {
        return tourRepo.findByIsCustomTrueAndStatus(status);
    }

    @Override
    public List<Tour> findByIsCustomFalse() {
        return tourRepo.findByIsCustomFalse();
    }
}
