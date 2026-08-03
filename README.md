# Journal Management Application

A secure RESTful Journal Management Application built using **Spring Boot** following the **Controller-Service-Repository** architecture. The application provides **Spring Security**, JWT authentication, Google OAuth2 login, journal management, weather integration, Redis caching, Kafka messaging, Docker deployment, and automated CI/CD.

![Journal Management Application](img.png)

## Features
- User Registration & Login
- Spring Security with Role-Based Authorization
- JWT Access Token & Refresh Token Authentication
- Google OAuth2 Login
- Journal CRUD Operations with MongoDB Atlas
- Public & Admin APIs
- Weather API Integration
- Redis Caching for External API Responses
- Apache Kafka for Asynchronous Email Processing
- Scheduled Cron Jobs
- Swagger/OpenAPI Documentation
- Spring Boot Actuator for Application Health Monitoring
- Logback Rolling File Logging
- Environment-Based Configuration
- Docker Containerization
- GitHub Actions CI/CD
- Production Deployment on Hostinger VPS using Docker & Nginx

## Tech Stack
- Java 11
- Spring Boot 2.7.16
- **Spring Security**
- JWT (Access & Refresh Tokens)
- OAuth2 (Google Login)
- MongoDB Atlas
- Spring Data MongoDB
- Redis
- Apache Kafka
- Swagger / OpenAPI 3
- JUnit 5
- Docker
- GitHub Actions
- Nginx
- Linux

##  Architecture
```text
client
    │
Controller
    │
Service
    │
Repository
    │
MongoDB Atlas
```

## Security
- Spring Security
- JWT Access & Refresh Token Authentication
- BCrypt Password Encryption
- Google OAuth2 Login
- Role-Based Authorization
- Secure REST APIs

## DevOps & Deployment
- Docker Containerized Application
- GitHub Actions CI/CD Pipeline
- Automatic Deployment to Hostinger VPS
- Nginx Reverse Proxy with HTTPS
- Environment Variable Based Configuration

## API Documentation
Swagger UI is available after running the application:

```text
http://localhost:8080/journalapp/swagger-ui/index.html
```

## API Endpoints
```text
POST   /public/signup
POST   /public/login
GET    /journal
POST   /journal
GET    /journal/id/{id}
GET    /journal
DELETE /journal/id/{id}
GET    /admin/all-users
POST   /admin/create-admin-user
PUT    /user
DELETE /user
GET    /user/city/{city}
```

## Run Locally
```bash
git clone https://github.com/divinearcs1-star/journal-app.git
cd journal-app
# Configure environment variables
mvn clean install
mvn spring-boot:run
```

## Run with Docker
```bash
docker build -t journal-app .
docker run -p 8080:8080 journal-app
```
Or, if using Docker Compose:

```bash
docker compose up -d
```

## Environment Variables
Configure the following environment variables before running the application:

```text
EMAIL_ID=
EMAIL_PASS=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
KAFKA_SERVER=
KAFKA_USERNAME=
KAFKA_PASSWORD=
MONGO_URI=
SERVER_PORT=
WEATHER_API=
REDIS_HOST=
REDIS_PORT=
SPRING_PROFILES_ACTIVE=dev
```
> The application reads these values from the system environment or the deployment platform (Docker, VPS, CI/CD, etc.).

## Highlights
- RESTful API Development using Spring Boot
- Spring Security with JWT & OAuth2 Authentication
- Layered Architecture (Controller-Service-Repository)
- MongoDB Atlas Integration
- Redis Caching
- Apache Kafka Messaging
- Weather API Integration
- Dockerized Deployment
- Automated CI/CD Pipeline using GitHub Actions

## Spring Profiles
The application supports multiple Spring profiles for different environments.
- `dev` – Local development
- `prod` – Production deployment

## Author
**Pankaj Belote**
