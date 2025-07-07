-- Migration to add new columns to tours table for enhanced tour management
-- Adds status, is_custom, and available_spots columns

ALTER TABLE tours 
ADD COLUMN status ENUM('INCOMPLETE', 'DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'INCOMPLETE',
ADD COLUMN is_custom BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN available_spots INTEGER NOT NULL DEFAULT 0;
