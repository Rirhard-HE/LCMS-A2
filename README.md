# LCMS - Legal Case Management System

## Overview

The Legal Case Management System (LCMS) is a web-based platform designed to help lawyers and administrators manage cases, evidences, categories, and hearings.
It provides secure authentication, file upload, and a Vue-based dashboard, with a Spring Boot backend for business logic and database operations.

---

## Features

* Case management: create, update, delete, and view cases; assign categories; track status; manage hearings.
* Evidence management: upload PDF/DOC files; update and delete evidences; secure static file access.
* User authentication: Spring Security with JWT; role-based access control.
* Frontend: Vue 2 + Element UI with pagination and responsive design.

---

## Tech Stack

* Frontend: Vue 2, Element UI, Axios
* Backend: Java 17, Spring Boot 3, Spring Security (JWT), MyBatis-Plus, MySQL (Azure)
* Deployment: GitHub Actions (CI/CD), AWS EC2 with pm2, Nginx

---

## Getting Started

### Backend

```bash
cd lcms-backend
mvn clean install
mvn spring-boot:run
```

Runs on: [http://localhost:8081](http://localhost:8081)

### Frontend

```bash
cd lcms-frontend
npm install
npm run serve
```

Runs on: [http://localhost:8080](http://localhost:8080)

---

## Testing

### Unit Tests

Backend functional testing with JUnit 5 and SpringBootTest.
Covers:

* Create case
* Update case
* Delete case
* List cases
* Get case by ID

Run tests:

```bash
mvn test
```

### API Testing

All backend APIs were tested with Postman.
Screenshots and exported collections are provided.

---
---

## Project Structure

```
lcms-frontend/   # Vue frontend
lcms-backend/    # Spring Boot backend
 ├── src/main/java/com/lcms/system   # core code
 └── src/test/java/com/lcms/system   # unit tests
```

---

## Contributors

* GeSitu – Backend and Database
* GeSitu,Devansh,Yuntan – Frontend and Figma
* Umangii -- PostmanTesting

---

## License

For educational use only (QUT IFN636 Assignment 2).

