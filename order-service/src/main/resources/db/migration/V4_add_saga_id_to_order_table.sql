ALTER TABLE orders
    ADD COLUMN saga_id UUID;

CREATE UNIQUE INDEX uk_orders_saga_id
    ON orders(saga_id)
    WHERE saga_id IS NOT NULL;