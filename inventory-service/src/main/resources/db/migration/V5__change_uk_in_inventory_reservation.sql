ALTER TABLE inventory_reservations
DROP CONSTRAINT uk_reservation_order_product;

ALTER TABLE inventory_reservations
ADD CONSTRAINT uk_reservation_order_saga_product
UNIQUE (order_id, saga_id, product_id);