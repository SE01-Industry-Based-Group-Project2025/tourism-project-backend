-- Fix bookings table - add missing columns conditionally
-- This migration adds columns only if they don't exist

-- Add checkout_session_id if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'bookings' 
  AND COLUMN_NAME = 'checkout_session_id';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE bookings ADD COLUMN checkout_session_id VARCHAR(255) UNIQUE', 
    'SELECT "checkout_session_id already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add payment_intent_id if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'bookings' 
  AND COLUMN_NAME = 'payment_intent_id';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE bookings ADD COLUMN payment_intent_id VARCHAR(255)', 
    'SELECT "payment_intent_id already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add special_note if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'bookings' 
  AND COLUMN_NAME = 'special_note';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE bookings ADD COLUMN special_note TEXT', 
    'SELECT "special_note already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add version if it doesn't exist
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'bookings' 
  AND COLUMN_NAME = 'version';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE bookings ADD COLUMN version BIGINT DEFAULT 0', 
    'SELECT "version already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Modify status column from ENUM to VARCHAR if needed
ALTER TABLE bookings MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Add index for checkout_session_id if it doesn't exist
SET @index_exists = 0;
SELECT COUNT(*) INTO @index_exists 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'bookings' 
  AND INDEX_NAME = 'idx_checkout_session';

SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_checkout_session ON bookings(checkout_session_id)', 
    'SELECT "idx_checkout_session already exists" as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
