CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        customer_id BIGINT NOT NULL,

                        order_status VARCHAR(50) NOT NULL,

                        recipient_name VARCHAR(255) NOT NULL,
                        phone_number VARCHAR(50) NOT NULL,
                        province VARCHAR(100) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        address_line VARCHAR(500) NOT NULL,
                        postal_code VARCHAR(20) NOT NULL,

                        total_amount NUMERIC(19, 2) NOT NULL,
                        currency VARCHAR(3) NOT NULL,

                        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        updated_at TIMESTAMP WITH TIME ZONE,

                        created_by VARCHAR(255) NOT NULL,
                        updated_by VARCHAR(255)
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,

                             order_id BIGINT NOT NULL,

                             product_id BIGINT NOT NULL,
                             product_name_snapshot VARCHAR(255) NOT NULL,

                             unit_price NUMERIC(19, 2) NOT NULL,
                             unit_price_currency VARCHAR(3) NOT NULL,

                             sub_total NUMERIC(19, 2) NOT NULL,
                             quantity INTEGER NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE
);

CREATE INDEX idx_orders_customer_id
    ON orders(customer_id);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);