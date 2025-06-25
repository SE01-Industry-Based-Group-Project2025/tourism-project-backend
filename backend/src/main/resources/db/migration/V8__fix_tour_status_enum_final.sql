-- V8__fix_tour_status_enum_final.sql
-- This migration safely updates the tour status column to only allow Incomplete, Ongoing, Completed

-- First, check if the flyway_schema_history table has a failed migration record and remove it
DELETE FROM flyway_schema_history WHERE version = '7' AND success = 0;

-- Update any tours that have invalid status values to 'Incomplete'
UPDATE tours SET status = 'Incomplete' 
WHERE status NOT IN ('Incomplete', 'Ongoing', 'Completed');

-- Now modify the column to restrict to only the allowed enum values
ALTER TABLE tours MODIFY COLUMN status ENUM('Incomplete', 'Ongoing', 'Completed') NOT NULL DEFAULT 'Incomplete';
