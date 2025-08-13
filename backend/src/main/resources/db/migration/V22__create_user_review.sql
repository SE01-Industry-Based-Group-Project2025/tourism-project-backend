-- 1. Ensure tours and users tables exist before this runs!

-- 2. Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment VARCHAR(1000),
    rating INT,
    created_at DATETIME,
    tour_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_review_tour FOREIGN KEY (tour_id) REFERENCES tours(id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id)
);
