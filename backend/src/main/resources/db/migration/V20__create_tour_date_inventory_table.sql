-- Create tour_date_inventory table for managing capacity per date
CREATE TABLE tour_date_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    date DATE NOT NULL,
    capacity INT NOT NULL,
    booked INT NOT NULL DEFAULT 0,
    
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_tour_date (tour_id, date),
    INDEX idx_inventory_tour (tour_id),
    INDEX idx_inventory_date (date)
);
