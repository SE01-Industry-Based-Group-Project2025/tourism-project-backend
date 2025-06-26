-- Fix tour status values to match Java enum (uppercase)
UPDATE tours SET status = 'INCOMPLETE' WHERE status = 'Incomplete';
UPDATE tours SET status = 'UPCOMING' WHERE status = 'upcoming';
UPDATE tours SET status = 'ONGOING' WHERE status = 'ongoing' OR status = 'Ongoing';
UPDATE tours SET status = 'COMPLETED' WHERE status = 'completed' OR status = 'Completed';

-- Set default status for any NULL values
UPDATE tours SET status = 'INCOMPLETE' WHERE status IS NULL;
