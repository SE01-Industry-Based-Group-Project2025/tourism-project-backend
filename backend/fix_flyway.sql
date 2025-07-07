-- Connect to database and fix flyway state
USE sl_tourpal;

-- Remove any failed migration records for version 7
DELETE FROM flyway_schema_history WHERE version = '7';

-- Update any invalid status values to valid ones
UPDATE tours SET status = 'Incomplete' 
WHERE status IS NOT NULL AND status NOT IN ('Incomplete', 'Ongoing', 'Completed');

-- Set any NULL status values to 'Incomplete'
UPDATE tours SET status = 'Incomplete' WHERE status IS NULL;

-- Check the current status values
SELECT DISTINCT status FROM tours;

-- Check flyway schema history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
