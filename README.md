# Home Services Marketplace

Distributed Systems assignment implementing a microservices-based home services marketplace.

Services:
- User Service (auth, wallet) — port 8080
- Service Catalog Service (categories, offers) — port 8081
- Booking Service (bookings, wallet validation) — port 8082
- Notification Service (consume RabbitMQ events, store notifications) — port 8083

Infrastructure:
- RabbitMQ (5672, 15672)
- PostgreSQL databases for each service (5433..5436)

Run:
1. Start infrastructure: `docker-compose up -d`
2. Start services: `mvn spring-boot:run` in each service folder

See `docker-compose.yml` and each service `application.yml` for details.
