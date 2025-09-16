# NFSe API

API for generating, canceling, and consulting electronic service invoices (NFSe) for 123 Milhas.

## Features
- JWT Authentication
- NFSe Generation
- NFSe Cancellation
- RPS Consultation

## Technologies
- Spring Boot 3.2.2
- Java 21
- JWT
- Spring Security

## API Endpoints
- POST /api/v1/tokens/generate
- POST /api/v1/nfse
- DELETE /api/v1/nfse/{numeroNfse}
- GET /api/v1/nfse/rps/{numeroRps}