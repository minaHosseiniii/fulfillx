ALTER TABLE outbox_events
    ADD COLUMN saga_id UUID;


CREATE INDEX idx_outbox_saga_id
    ON outbox_events (saga_id);


CREATE UNIQUE INDEX uk_outbox_aggregate_saga_event
    ON outbox_events (
                      aggregate_id,
                      saga_id,
                      event_type
        )
    WHERE saga_id IS NOT NULL;