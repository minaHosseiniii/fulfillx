ALTER TABLE notifications
    ADD COLUMN lease_until TIMESTAMP;

ALTER TABLE notifications
    ADD COLUMN processing_token UUID;