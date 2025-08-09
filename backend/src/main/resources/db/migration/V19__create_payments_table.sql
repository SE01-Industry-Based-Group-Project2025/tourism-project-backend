-- Create payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    provider VARCHAR(50) NOT NULL DEFAULT 'STRIPE',
    provider_payment_intent_id VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    receipt_url VARCHAR(500),
    raw_event TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    
    INDEX idx_payments_booking (booking_id),
    INDEX idx_payments_provider_intent (provider_payment_intent_id),
    INDEX idx_payments_status (status)
);
