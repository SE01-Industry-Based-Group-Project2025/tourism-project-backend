-- V4__add_missing_tour_highlights_table.sql

-- Tour highlights (ElementCollection)
CREATE TABLE IF NOT EXISTS tour_highlights (
    tour_id BIGINT NOT NULL,
    highlight VARCHAR(500),
    FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE
);
