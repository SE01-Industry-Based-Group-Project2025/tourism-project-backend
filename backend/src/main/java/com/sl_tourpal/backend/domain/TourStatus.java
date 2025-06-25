package com.sl_tourpal.backend.domain;

/**
 * Enum representing the different statuses a tour can have
 */
public enum TourStatus {
    INCOMPLETE("Incomplete"),
    ONGOING("Ongoing"),
    COMPLETED("Completed");

    private final String displayName;

    TourStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
