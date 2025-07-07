-- Final comprehensive fix for ALL tour status values
-- This migration will force ALL status values to be uppercase regardless of current case

-- First, let's see what we're working with by updating everything step by step
-- Convert any form of 'incomplete' to 'INCOMPLETE'
UPDATE tours SET status = 'INCOMPLETE' WHERE UPPER(TRIM(status)) = 'INCOMPLETE';

-- Convert any form of 'upcoming' to 'UPCOMING'  
UPDATE tours SET status = 'UPCOMING' WHERE UPPER(TRIM(status)) = 'UPCOMING';

-- Convert any form of 'ongoing' to 'ONGOING'
UPDATE tours SET status = 'ONGOING' WHERE UPPER(TRIM(status)) = 'ONGOING';

-- Convert any form of 'completed' to 'COMPLETED'
UPDATE tours SET status = 'COMPLETED' WHERE UPPER(TRIM(status)) = 'COMPLETED';

-- Handle any NULL, empty, or invalid status values
UPDATE tours SET status = 'INCOMPLETE' 
WHERE status IS NULL 
   OR TRIM(status) = '' 
   OR UPPER(TRIM(status)) NOT IN ('INCOMPLETE', 'UPCOMING', 'ONGOING', 'COMPLETED');
