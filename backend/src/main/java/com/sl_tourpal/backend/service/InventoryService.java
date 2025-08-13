package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.TourDateInventory;

import java.time.LocalDate;

/**
 * Service for managing tour date inventory and capacity
 */
public interface InventoryService {
    
    /**
     * Get or create inventory for a tour on a specific date
     * @param tourId the tour ID
     * @param date the date
     * @return the tour date inventory
     */
    TourDateInventory getOrCreateInventory(Long tourId, LocalDate date);
    
    /**
     * Get or create inventory with pessimistic lock for updates
     * @param tourId the tour ID
     * @param date the date
     * @return the tour date inventory with lock
     */
    TourDateInventory getOrCreateInventoryForUpdate(Long tourId, LocalDate date);
    
    /**
     * Assert that there is enough capacity for the requested guests
     * @param inventory the inventory to check
     * @param requestedGuests number of guests requested
     * @throws IllegalStateException if not enough capacity
     */
    void assertCapacity(TourDateInventory inventory, int requestedGuests);
    
    /**
     * Commit booking by updating the booked count
     * @param inventory the inventory to update
     * @param guestCount number of guests to book
     */
    void commitBooking(TourDateInventory inventory, int guestCount);
}
