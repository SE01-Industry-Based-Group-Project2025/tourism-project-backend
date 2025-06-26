-- Comprehensive fix for tour status values to match Java enum (all uppercase)

-- Update all possible case variations to correct uppercase enum values
UPDATE tours SET status = 'INCOMPLETE' WHERE UPPER(status) = 'INCOMPLETE' AND status != 'INCOMPLETE';
UPDATE tours SET status = 'UPCOMING' WHERE UPPER(status) = 'UPCOMING' AND status != 'UPCOMING';
UPDATE tours SET status = 'ONGOING' WHERE UPPER(status) = 'ONGOING' AND status != 'ONGOING';
UPDATE tours SET status = 'COMPLETED' WHERE UPPER(status) = 'COMPLETED' AND status != 'COMPLETED';

-- Handle any NULL or empty values
UPDATE tours SET status = 'INCOMPLETE' WHERE status IS NULL OR status = '';

-- Verify no invalid status values remain
-- Any status not in the enum will cause issues, so ensure all are valid
UPDATE tours SET status = 'INCOMPLETE' 
WHERE status NOT IN ('INCOMPLETE', 'UPCOMING', 'ONGOING', 'COMPLETED');