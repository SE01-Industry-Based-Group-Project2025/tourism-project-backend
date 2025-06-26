package com.sl_tourpal.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.domain.*;
import com.sl_tourpal.backend.repository.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepo;
    public TourServiceImpl(TourRepository tourRepo) {
        this.tourRepo = tourRepo;
    }

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
        tour.setIsCustom(req.getIsCustom() != null ? req.getIsCustom() : false);
        tour.setAvailableSpots(req.getAvailableSpots() != null ? req.getAvailableSpots() : 0);
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
        existingTour.setIsCustom(req.getIsCustom() != null ? req.getIsCustom() : false);
        existingTour.setAvailableSpots(req.getAvailableSpots() != null ? req.getAvailableSpots() : 0);
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
}
