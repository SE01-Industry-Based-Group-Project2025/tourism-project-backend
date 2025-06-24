package com.sl_tourpal.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sl_tourpal.backend.dto.*;
import com.sl_tourpal.backend.domain.*;
import com.sl_tourpal.backend.repository.*;
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

        // map itinerary
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

        // map accommodations
        tour.setAccommodations(
            req.getAccommodations().stream().map(dto -> {
                Accommodation a = new Accommodation();
                a.setTitle(dto.getTitle());
                a.setDescription(dto.getDescription());
                a.setImages(dto.getImages());
                a.setTour(tour);
                return a;
            }).collect(Collectors.toList())
        );

        // map pricing tiers
        tour.setPricingTiers(
            req.getPricingTiers().stream().map(dto -> {
                PricingTier p = new PricingTier();
                p.setGroupType(dto.getGroupType());
                p.setPrice(dto.getPrice());
                p.setTour(tour);
                return p;
            }).collect(Collectors.toList())
        );

        // map availability
        tour.setAvailabilityRanges(
            req.getAvailabilityRanges().stream().map(dto -> {
                AvailabilityRange ar = new AvailabilityRange();
                ar.setStartDate(dto.getStartDate());
                ar.setEndDate(dto.getEndDate());
                ar.setTour(tour);
                return ar;
            }).collect(Collectors.toList())
        );

        // map images
        tour.setImages(
            req.getImages().stream().map(dto -> {
                TourImage img = new TourImage();
                img.setUrl(dto.getUrl());
                img.setPrimary(dto.isPrimary());
                img.setTour(tour);
                return img;
            }).collect(Collectors.toList())
        );

        // saves tour + cascades
        return tourRepo.save(tour);
    }
}
