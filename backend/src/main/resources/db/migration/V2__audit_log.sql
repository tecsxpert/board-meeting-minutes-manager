-- V2__audit_log.sql: Creates the audit_log table to track all entity changes (CREATE, UPDATE, DELETE).
-- Uses UUID primary key and indexes entity_id for fast lookups by entity.

CREATE TABLE audit_log (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type  VARCHAR(50)  NOT NULL,
    entity_id    UUID         NOT NULL,
    action       VARCHAR(20)  NOT NULL,
    performed_by VARCHAR(100),
    performed_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Constraint to enforce valid action values
ALTER TABLE audit_log
    ADD CONSTRAINT chk_audit_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE'));

-- Index on entity_id for fast lookups per entity
CREATE INDEX idx_audit_log_entity_id ON audit_log (entity_id);

-- Index on entity_type + entity_id for compound queries
CREATE INDEX idx_audit_log_entity_type_id ON audit_log (entity_type, entity_id);

-- Index on performed_at for time-range queries
CREATE INDEX idx_audit_log_performed_at ON audit_log (performed_at);