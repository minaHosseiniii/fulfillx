CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(100) NOT NULL,
                               aggregate_id BIGINT NOT NULL,
                               event_type VARCHAR(100) NOT NULL,
                               payload TEXT NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               published_at TIMESTAMP WITH TIME ZONE,
                               attempts INTEGER NOT NULL DEFAULT 0,
                               last_error TEXT
);

CREATE INDEX idx_outbox_events_pending
    ON outbox_events(created_at)
    WHERE published_at IS NULL;