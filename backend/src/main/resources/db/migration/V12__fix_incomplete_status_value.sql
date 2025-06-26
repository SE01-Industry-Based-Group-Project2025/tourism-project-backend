-- Fix the specific 'Incomplete' value that's causing the enum mismatch error

-- Update the exact case variations found in the database
UPDATE tours SET status = 'INCOMPLETE' WHERE status = 'Incomplete';
UPDATE tours SET status = 'UPCOMING' WHERE status = 'Upcoming';  
UPDATE tours SET status = 'ONGOING' WHERE status = 'Ongoing';
UPDATE tours SET status = 'COMPLETED' WHERE status = 'Completed';

-- Handle any other possible variations
UPDATE tours SET status = 'INCOMPLETE' WHERE LOWER(status) = 'incomplete' AND status != 'INCOMPLETE';
UPDATE tours SET status = 'UPCOMING' WHERE LOWER(status) = 'upcoming' AND status != 'UPCOMING';
UPDATE tours SET status = 'ONGOING' WHERE LOWER(status) = 'ongoing' AND status != 'ONGOING';
UPDATE tours SET status = 'COMPLETED' WHERE LOWER(status) = 'completed' AND status != 'COMPLETED';

-- Set default for any NULL or invalid values
UPDATE tours SET status = 'INCOMPLETE' WHERE status IS NULL OR status NOT IN ('INCOMPLETE', 'UPCOMING', 'ONGOING', 'COMPLETED');
