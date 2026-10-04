CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,

                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           phone VARCHAR(30) NOT NULL,
                           status VARCHAR(20) NOT NULL,

                           created_by VARCHAR(100) NOT NULL,
                           created_at TIMESTAMP NOT NULL,

                           updated_by VARCHAR(100),
                           updated_at TIMESTAMP
);