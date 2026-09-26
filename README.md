# FulfillX

Microservices-based order fulfillment platform built with Java and Spring Boot.

## Tech Stack

* Java 17
* Spring Boot 4
* Spring Data JPA / Hibernate
* PostgreSQL
* Flyway
* MapStruct
* Bean Validation
* OpenAPI / Swagger
* JUnit 5 / MockMvc
* Docker

## Services

Currently implemented:

* Order Service

More services will be added as the project evolves.

## Order Service

### Create Order

```text
POST /api/v1/orders
```

Creates a new order with its items and shipping address.

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Run Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run the Order Service:

```bash
cd order-service
mvn spring-boot:run
```

Run tests:

```bash
mvn clean test
```

## Postman

The Postman collection is available in:

```text
postman/FulfillX.postman_collection.json
```

Import it into Postman to test the available APIs.

## Project Structure

```text
fulfillx/
├── order-service/
├── postman/
├── docker-compose.yml
└── README.md
```
