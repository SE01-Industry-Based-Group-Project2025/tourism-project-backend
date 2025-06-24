-- V3__fix_pricing_tiers_price_column.sql

-- Alter pricing_tiers table to use DECIMAL instead of DOUBLE for price column
ALTER TABLE pricing_tiers MODIFY COLUMN price DECIMAL(10,2);
