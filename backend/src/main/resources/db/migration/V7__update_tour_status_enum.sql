-- V7__update_tour_status_enum.sql
-- Update existing status values and then restrict the ENUM to only allow Incomplete, Ongoing, Completed

-- First, update any invalid status values to 'Incomplete'
UPDATE tours SET status = 'Incomplete' WHERE status NOT IN ('Incomplete', 'Ongoing', 'Completed');

-- Now safely alter the column to restrict the ENUM values
ALTER TABLE tours MODIFY COLUMN status ENUM('Incomplete', 'Ongoing', 'Completed') NOT NULL DEFAULT 'Incomplete';
