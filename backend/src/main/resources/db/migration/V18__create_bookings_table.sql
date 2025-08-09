-- Create bookings table
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tour_id BIGINT NOT NULL,
    tour_name_snapshot VARCHAR(255) NOT NULL,
    unit_price_snapshot DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    selected_date DATE NOT NULL,
    guest_count INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    checkout_session_id VARCHAR(255) UNIQUE,
    payment_intent_id VARCHAR(255),
    special_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE,
    
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_tour (tour_id),
    INDEX idx_bookings_date (selected_date),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_checkout_session (checkout_session_id),
    INDEX idx_bookings_created_at (created_at)
);
