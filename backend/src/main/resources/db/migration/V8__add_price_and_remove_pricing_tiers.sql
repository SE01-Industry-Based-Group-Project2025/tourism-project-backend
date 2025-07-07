-- V8__add_price_and_remove_pricing_tiers.sql
-- Add price column to tours table and remove pricing_tiers table

-- Add price column to tours table
ALTER TABLE tours 
ADD COLUMN price DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Drop the pricing_tiers table as it's no longer needed
DROP TABLE IF EXISTS pricing_tiers;
