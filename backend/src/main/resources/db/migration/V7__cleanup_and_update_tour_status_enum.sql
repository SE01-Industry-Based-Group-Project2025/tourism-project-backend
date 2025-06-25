-- V7__cleanup_and_update_tour_status_enum.sql
-- Update any invalid status values to valid ones first, then alter the enum

-- Update tours with invalid status values
UPDATE tours SET status = 'Incomplete' 
WHERE status IS NOT NULL AND status NOT IN ('Incomplete', 'Ongoing', 'Completed');

-- Set any NULL status values to 'Incomplete'
UPDATE tours SET status = 'Incomplete' WHERE status IS NULL;

-- Now safely alter the column to restrict the ENUM values
ALTER TABLE tours MODIFY COLUMN status ENUM('Incomplete', 'Ongoing', 'Completed') NOT NULL DEFAULT 'Incomplete';
