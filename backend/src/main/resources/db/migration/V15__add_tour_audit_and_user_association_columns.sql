-- Migration to add audit and user association columns to tours table
-- Adds created_by_user_id, created_at, and updated_at columns for tour tracking

ALTER TABLE tours 
ADD COLUMN created_by_user_id BIGINT NULL,
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP;

-- Add foreign key constraint to link tours with the user who created them
ALTER TABLE tours 
ADD CONSTRAINT fk_tours_created_by_user 
FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL;

-- Create index for better query performance on created_by_user_id
CREATE INDEX idx_tours_created_by_user ON tours(created_by_user_id);

-- Create index for better query performance on created_at
CREATE INDEX idx_tours_created_at ON tours(created_at);
