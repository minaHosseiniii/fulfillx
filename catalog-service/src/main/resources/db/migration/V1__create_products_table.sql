CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,

                          name VARCHAR(150) NOT NULL,
                          description VARCHAR(500),
                          sku VARCHAR(100) NOT NULL UNIQUE,

                          price NUMERIC(19, 2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,

                          status VARCHAR(20) NOT NULL,

                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP
);