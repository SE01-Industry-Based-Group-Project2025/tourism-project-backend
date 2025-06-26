-- Convert status column to accept any string values and set default
-- This eliminates enum constraints and allows flexible status values

-- Remove any existing enum constraints and ensure column is VARCHAR
ALTER TABLE tours MODIFY COLUMN status VARCHAR(50) DEFAULT 'Incomplete';
