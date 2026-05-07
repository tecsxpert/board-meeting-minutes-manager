-- V1__init.sql: Creates the board_minutes table with all required columns, indexes, and auto-update trigger.
-- Uses UUID primary key, soft delete via deleted_at, and status enum values: DRAFT, PUBLISHED, ARCHIVED.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE board_minutes (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(255)  NOT NULL,
    meeting_date        DATE          NOT NULL,
    attendees           TEXT,
    content             TEXT,
    status              VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    ai_description      TEXT,
    ai_recommendations  TEXT,
    deleted_at          TIMESTAMP,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Constraint to enforce valid status values
ALTER TABLE board_minutes
    ADD CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'));

-- Index on status for filtering queries
CREATE INDEX idx_board_minutes_status       ON board_minutes (status);

-- Index on meeting_date for date-range queries
CREATE INDEX idx_board_minutes_meeting_date ON board_minutes (meeting_date);

-- Index on deleted_at to speed up soft-delete filtering
CREATE INDEX idx_board_minutes_deleted_at   ON board_minutes (deleted_at);

-- Function to auto-update updated_at on every row modification
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger that calls fn_set_updated_at before any UPDATE on board_minutes
CREATE TRIGGER trg_board_minutes_updated_at
    BEFORE UPDATE ON board_minutes
    FOR EACH ROW
    EXECUTE PROCEDURE fn_set_updated_at();