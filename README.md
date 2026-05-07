# Tool-99 — Board Meeting Minutes Manager

> Tool-99 — AI-powered Board Meeting Minutes Manager | Capstone Project | Sprint: 14 April – 9 May 2026

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Browser                              │
│                    http://localhost                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                  React Frontend (Port 80)                   │
│              React 18 + Vite + Tailwind CSS                 │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP (Axios)
┌──────────────────────▼──────────────────────────────────────┐
│             Spring Boot Backend (Port 8080)                 │
│         Java 17 + Spring Security + JWT + Redis             │
└────────┬─────────────────────────────┬───────────────────────┘
         │                             │
┌────────▼────────┐          ┌─────────▼──────────┐
│  PostgreSQL 15  │          │  Flask AI Service  │
│   (Port 5432)   │          │    (Port 5000)     │
│   Primary DB    │          │  Groq LLaMA-3.3    │
└─────────────────┘          └────────────────────┘
         │
┌────────▼────────┐
│    Redis 7      │
│   (Port 6379)   │
│  Cache Layer    │
└─────────────────┘
```

---

## Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 17                           |
| Framework   | Spring Boot 3.2.5                 |
| Database    | PostgreSQL 15                     |
| Cache       | Redis 7                           |
| Migrations  | Flyway                            |
| Security    | Spring Security + JWT             |
| AI          | Flask + Groq API (LLaMA-3.3-70b)  |
| Frontend    | React 18 + Vite + Tailwind CSS    |
| HTTP Client | Axios                             |
| Containers  | Docker + Docker Compose           |

---

## 📁 Project Structure

```
board-meeting-minutes-manager/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/internship/tool/
│   │   ├── config/                   # Security, Redis, Mail, Swagger, DataSeeder
│   │   ├── controller/               # REST endpoints
│   │   ├── entity/                   # JPA table models
│   │   ├── exception/                # Custom exceptions + GlobalExceptionHandler
│   │   ├── repository/               # DB queries
│   │   └── service/                  # Business logic
│   ├── src/main/resources/
│   │   ├── db/migration/             # Flyway SQL migrations
│   │   └── application.yml           # App configuration
│   └── pom.xml
├── ai-service/                       # Flask Python microservice
│   ├── routes/                       # API endpoint files
│   ├── services/                     # Groq client
│   ├── prompts/                      # Prompt templates
│   └── app.py
├── frontend/                         # React + Vite frontend
│   └── src/
│       ├── components/               # Reusable UI components
│       ├── context/                  # Auth context
│       ├── pages/                    # Page components
│       └── services/                 # API service calls
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 🗄️ Database Schema

**Table: `meeting_minutes`**

| Column               | Type          | Description                          |
|----------------------|---------------|--------------------------------------|
| `id`                 | BIGSERIAL     | Auto-increment primary key           |
| `title`              | VARCHAR(255)  | Meeting title (required)             |
| `meeting_date`       | DATE          | Date of the meeting (required)       |
| `attendees`          | TEXT          | Comma-separated list of attendees    |
| `agenda`             | TEXT          | Meeting agenda                       |
| `content`            | TEXT          | Full meeting minutes (required)      |
| `status`             | VARCHAR(50)   | DRAFT / PUBLISHED / ARCHIVED         |
| `ai_description`     | TEXT          | AI-generated summary                 |
| `ai_recommendations` | TEXT          | AI-generated action items            |
| `ai_report`          | TEXT          | Full AI-generated report             |
| `is_deleted`         | BOOLEAN       | Soft-delete flag                     |
| `created_at`         | TIMESTAMP     | Record creation time                 |
| `updated_at`         | TIMESTAMP     | Last update time                     |

---

## 🚀 Getting Started

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- Java 17+ (for local development without Docker)
- Maven 3.8+

---

### 1. Clone the Repository

```bash
git clone <repository-url>
cd board-meeting-minutes-manager
```

---

### 2. Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` and fill in your values:

| Variable | Description | Example |
|----------|-------------|---------|
| `POSTGRES_DB` | Database name | `boardminutes` |
| `POSTGRES_USER` | DB username | `postgres` |
| `POSTGRES_PASSWORD` | DB password | `yourpassword` |
| `POSTGRES_PORT` | DB port | `5432` |
| `SERVER_PORT` | Backend port | `8080` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `JWT_SECRET` | JWT secret key (min 32 chars) | `mySecretKey1234567890abcdefghij` |
| `JWT_EXPIRATION_MS` | Token expiry in ms | `86400000` |
| `MAIL_HOST` | SMTP host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | Email address | `your@gmail.com` |
| `MAIL_PASSWORD` | Gmail App Password | `xxxx xxxx xxxx xxxx` |
| `AI_SERVICE_URL` | AI service URL | `http://ai-service:5000` |
| `GROQ_API_KEY` | Groq API key | `gsk_...` |

> ⚠️ **Never commit your `.env` file.** It is already listed in `.gitignore`.

---

### 3. Run with Docker Compose

```bash
# Start all services
docker-compose up --build -d

# View logs
docker-compose logs -f backend

# Stop all services
docker-compose down

# Fresh restart (wipes DB)
docker-compose down -v
docker-compose up --build -d
```

---

### 4. Verify Application is Running

| Service | URL | Expected |
|---------|-----|----------|
| Frontend | http://localhost | Login page |
| Backend Health | http://localhost:8080/actuator/health | `{"status":"UP"}` |
| Swagger UI | http://localhost:8080/swagger-ui.html | API docs |
| AI Health | http://localhost:5000/health | `{"status":"ok"}` |

---

## 🔐 API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| POST | `/api/auth/refresh` | Refresh JWT token |

### Meeting Minutes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/minutes` | Get all minutes (paginated) |
| GET | `/api/minutes/{id}` | Get by ID |
| POST | `/api/minutes` | Create new minutes |
| PUT | `/api/minutes/{id}` | Update minutes |
| DELETE | `/api/minutes/{id}` | Soft delete |
| GET | `/api/minutes/search?q=` | Search by title/content |
| GET | `/api/minutes/filter/status?status=` | Filter by status |
| GET | `/api/minutes/filter/date-range?start=&end=` | Filter by date range |

---

## 🤖 AI Features

The application integrates with Groq API (LLaMA-3.3-70b) to provide:

- **Describe** — Auto-generated meeting summary
- **Recommend** — Action items extracted from minutes
- **Generate Report** — Full structured AI report

---

## 🔒 Security Features

- JWT-based authentication
- BCrypt password hashing
- Role-based access control (USER / ADMIN)
- CORS configured
- Input validation on all endpoints
- Soft delete pattern
- No hardcoded secrets — all via environment variables

---

## 🗃️ Database Migrations (Flyway)

| File | Description |
|------|-------------|
| `V1__init.sql` | Initial schema — meeting_minutes + users tables |
| `V2__audit_log.sql` | Audit log table |
| `V3__audit_log_indexes.sql` | Indexes for audit log |

---

## 👥 Team

| Role | Responsibilities |
|------|-----------------|
| Java Developer 1 | Spring Boot setup, Service layer, JWT auth, Docker Compose, Data seeder, README |
| Java Developer 2 | DB schema (Flyway), Repository layer, React frontend, Email notifications |
| AI Developer 1 | Flask setup, /describe and /recommend endpoints |
| AI Developer 2 | GroqClient, /generate-report, Security review |
| Security Reviewer | Security testing, SECURITY.md |

---

## 📜 License

MIT License — Capstone Project | Tool-99 | Sprint: 14 April – 9 May 2026
