ALTER TABLE customers
    ADD COLUMN keycloak_user_id VARCHAR(36) UNIQUE;