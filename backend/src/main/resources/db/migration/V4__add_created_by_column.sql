-- Add created_by column to meeting_minutes table
ALTER TABLE meeting_minutes 
ADD COLUMN created_by VARCHAR(100);

-- Create index on created_by for better query performance
CREATE INDEX idx_created_by ON meeting_minutes(created_by);

-- Update existing records to have a default created_by value
UPDATE meeting_minutes 
SET created_by = 'admin' 
WHERE created_by IS NULL;