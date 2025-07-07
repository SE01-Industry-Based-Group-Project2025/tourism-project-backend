-- V9__add_upcoming_status_to_tour_enum.sql
-- Add 'Upcoming' status to the tour status ENUM

-- Update the status column to include the new 'Upcoming' value
ALTER TABLE tours MODIFY COLUMN status ENUM('Incomplete', 'Upcoming', 'Ongoing', 'Completed') NOT NULL DEFAULT 'Incomplete';
