package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.TourDateInventory;
import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.repository.TourDateInventoryRepository;
import com.sl_tourpal.backend.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {
    
    private final TourDateInventoryRepository inventoryRepository;
    private final TourRepository tourRepository;
    
    @Override
    public TourDateInventory getOrCreateInventory(Long tourId, LocalDate date) {
        return inventoryRepository.findByTourIdAndDate(tourId, date)
                .orElseGet(() -> createNewInventory(tourId, date));
    }
    
    @Override
    public TourDateInventory getOrCreateInventoryForUpdate(Long tourId, LocalDate date) {
        return inventoryRepository.findByTourIdAndDateForUpdate(tourId, date)
                .orElseGet(() -> createNewInventory(tourId, date));
    }
    
    @Override
    public void assertCapacity(TourDateInventory inventory, int requestedGuests) {
        if (!inventory.hasCapacity(requestedGuests)) {
            throw new IllegalStateException(
                String.format("Not enough capacity. Available: %d, Requested: %d", 
                    inventory.getAvailableSpots(), requestedGuests));
        }
    }
    
    @Override
    public void commitBooking(TourDateInventory inventory, int guestCount) {
        inventory.bookGuests(guestCount);
        inventoryRepository.save(inventory);
        log.info("Committed booking for {} guests on {} for tour {}", 
                guestCount, inventory.getDate(), inventory.getTour().getId());
    }
    
    private TourDateInventory createNewInventory(Long tourId, LocalDate date) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found: " + tourId));
        
        TourDateInventory inventory = new TourDateInventory();
        inventory.setTour(tour);
        inventory.setDate(date);
        inventory.setCapacity(tour.getAvailableSpots());
        inventory.setBooked(0);
        
        TourDateInventory saved = inventoryRepository.save(inventory);
        log.info("Created new inventory for tour {} on date {} with capacity {}", 
                tourId, date, tour.getAvailableSpots());
        
        return saved;
    }
}
