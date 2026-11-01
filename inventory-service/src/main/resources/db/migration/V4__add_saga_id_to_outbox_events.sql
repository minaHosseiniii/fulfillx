ALTER TABLE outbox_events
    ADD COLUMN saga_id UUID;


CREATE INDEX idx_outbox_saga_id
    ON outbox_events (saga_id);