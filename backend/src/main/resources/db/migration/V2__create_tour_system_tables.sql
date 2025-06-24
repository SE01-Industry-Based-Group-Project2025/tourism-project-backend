-- V2__create_tour_system_tables.sql

-- Main tours table
CREATE TABLE IF NOT EXISTS tours (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(100),
    duration_value INTEGER,
    duration_unit VARCHAR(50),
    short_description VARCHAR(500),
    difficulty VARCHAR(50),
    region VARCHAR(100)
);

-- Tour activities (ElementCollection)
CREATE TABLE IF NOT EXISTS tour_activities (
    tour_id BIGINT NOT NULL,
    activity VARCHAR(255),
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);

-- Itinerary days
CREATE TABLE IF NOT EXISTS itinerary_days (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    day_number INTEGER,
    title VARCHAR(255),
    description TEXT,
    image_url VARCHAR(500),
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);

-- Itinerary destinations (ElementCollection)
CREATE TABLE IF NOT EXISTS itinerary_destinations (
    day_id BIGINT NOT NULL,
    destination VARCHAR(255),
    FOREIGN KEY (day_id) REFERENCES itinerary_days(id) ON DELETE CASCADE
);

-- Accommodations
CREATE TABLE IF NOT EXISTS accommodations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);

-- Accommodation images (ElementCollection)
CREATE TABLE IF NOT EXISTS accommodation_images (
    accommodation_id BIGINT NOT NULL,
    image_url VARCHAR(500),
    FOREIGN KEY (accommodation_id) REFERENCES accommodations(id) ON DELETE CASCADE
);

-- Pricing tiers
CREATE TABLE pricing_tiers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    group_type VARCHAR(50),
    price DECIMAL(10,2),
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);


-- Availability ranges
CREATE TABLE IF NOT EXISTS availability_ranges (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);

-- Tour images
CREATE TABLE IF NOT EXISTS tour_images (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    is_primary BOOLEAN DEFAULT FALSE,
    tour_id BIGINT NOT NULL,
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);
