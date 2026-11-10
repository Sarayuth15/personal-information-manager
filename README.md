# Personal Information Manager (PIM)

Receives messages from a Telegram bot, stores the original text in MySQL, and shows the messages in a web dashboard.

```
Telegram user → Telegram bot → Spring Boot webhook → MySQL → REST API → Next.js website
```

Messages are stored exactly as they arrive. Nothing is categorised, summarised, translated or rewritten.

> **Status:** Phase 2 (database and migrations). The Telegram integration, APIs, security and UI are built in later phases. See `CLAUDE.md` §12.
>
> Documentation: [database](docs/database.md).

## Repository layout

| Path | Contents |
|---|---|
| `backend/` | Spring Boot 4.1 (Java 21, Maven), package `com.pim` |
| `frontend/` | Next.js 16 App Router, React 19, TypeScript, Tailwind CSS 4, shadcn/ui, Lucide |
| `docs/ui-reference/` | UI designs. These are the source of truth for the frontend. |
| `docker-compose.yml` | MySQL in Docker. **Not used right now**; kept for deployment (Phase 10). |
| `.env.example` | Environment variables template |

## Prerequisites

- **JDK 21.** Set `JAVA_HOME` to it. `./mvnw` uses `JAVA_HOME`, not whichever `java` is first on your PATH.
- **Node.js 24** and npm 11
- **A MySQL 8 server** you can reach from this machine. Docker isn't required.

## Local development

### 1. Environment

```bash
cp .env.example .env    # then replace every placeholder value
```

`.env` is git-ignored. Never commit it.

### 2. Database

On your MySQL server, create the application database, a separate test database, and a user that can access both. See [docs/database.md](docs/database.md#setting-up-mysql) for the SQL. Then set the `MYSQL_*` values in `.env`.

If MySQL runs on another machine, set `MYSQL_SSL_MODE=REQUIRED` so the connection is encrypted.

### 3. Backend (http://localhost:8080)

```bash
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
./mvnw test
```

- On startup the backend reads the root `.env`, connects to MySQL and applies any pending Flyway migrations. It won't start if MySQL is unreachable.
- `./mvnw test` uses the **test database** (`MYSQL_TEST_DATABASE`), never `MYSQL_DATABASE`. Repository tests roll back after each test.

Health check: `GET http://localhost:8080/actuator/health` returns `{"status":"UP"}`.

### 4. Frontend (http://localhost:3000)

```bash
cd frontend
npm install
npm run dev
```

| Script | Purpose |
|---|---|
| `npm run dev` | Development server |
| `npm run build` / `npm start` | Production build and serve |
| `npm run lint` | ESLint |
| `npm run typecheck` | TypeScript check (no emit) |
| `npm test` | Vitest and React Testing Library (single run) |
| `npm run test:watch` | Vitest in watch mode |

## Environment variables

| Variable | Used by | Description |
|---|---|---|
| `MYSQL_HOST` | backend | MySQL host (default `localhost`) |
| `MYSQL_PORT` | backend | MySQL port (default `3306`) |
| `MYSQL_DATABASE` | backend | Application database |
| `MYSQL_TEST_DATABASE` | backend tests | Database used only by `./mvnw test` (default `pim_test`) |
| `MYSQL_USER` / `MYSQL_PASSWORD` | backend | Database user |
| `MYSQL_SSL_MODE` | backend | Connection encryption (default `PREFERRED`; use `REQUIRED` for a remote server) |
| `SERVER_PORT` | backend | HTTP port (default `8080`) |
| `MYSQL_ROOT_PASSWORD` | docker-compose only | Not used while Docker is off |

New variables (Telegram bot token, webhook secret, auth settings) will be added to this table and to `.env.example` in the phase that introduces them.
