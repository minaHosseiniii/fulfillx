CREATE TABLE inventory_reservations (
                                        id BIGSERIAL PRIMARY KEY,
                                        order_id BIGINT NOT NULL,
                                        saga_id UUID NOT NULL,
                                        product_id BIGINT NOT NULL,
                                        quantity INTEGER NOT NULL,
                                        status VARCHAR(20) NOT NULL,
                                        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                        updated_at TIMESTAMP WITH TIME ZONE,

                                        CONSTRAINT uk_reservation_order_product
                                            UNIQUE (order_id, product_id),

                                        CONSTRAINT chk_reservation_quantity
                                            CHECK (quantity > 0),

                                        CONSTRAINT chk_reservation_status
                                            CHECK (status IN ('RESERVED', 'RELEASED', 'DEDUCTED'))
);

CREATE INDEX idx_inventory_reservations_saga_id
    ON inventory_reservations (saga_id);

CREATE INDEX idx_inventory_reservations_order_id
    ON inventory_reservations (order_id);