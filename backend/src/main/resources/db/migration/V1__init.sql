CREATE TABLE meeting_minutes (
    id                   BIGSERIAL     PRIMARY KEY,
    title                VARCHAR(255)  NOT NULL,
    meeting_date         DATE          NOT NULL,
    attendees            TEXT,
    agenda               TEXT,
    content              TEXT          NOT NULL,
    status               VARCHAR(50)   NOT NULL DEFAULT 'DRAFT',
    ai_description       TEXT,
    ai_recommendations   TEXT,
    ai_report            TEXT,
    is_deleted           BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Indexes on key fields
CREATE INDEX idx_meeting_date ON meeting_minutes(meeting_date);
CREATE INDEX idx_status       ON meeting_minutes(status);
CREATE INDEX idx_is_deleted   ON meeting_minutes(is_deleted);

CREATE TABLE users (
    id         BIGSERIAL     PRIMARY KEY,
    username   VARCHAR(100)  NOT NULL UNIQUE,
    email      VARCHAR(255)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    role       VARCHAR(50)   NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP     NOT NULL DEFAULT NOW()
);
