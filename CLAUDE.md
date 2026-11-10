# CLAUDE.md — Personal Information Manager

## 1. Project Identity

Project Name: Personal Information Manager (PIM)

Project Type: Full-Stack Web Application

Purpose:

A personal information management application that receives messages from a Telegram Bot, stores the original messages in MySQL, and displays them through a modern web dashboard.

Core workflow:

Telegram User → Telegram Bot → Spring Boot Webhook → MySQL → REST API → Next.js Website

## 2. Your Role

Act as a Senior Software Engineer, Solution Architect, UI Engineer, and Technical Lead.

You are responsible for:

- Clean and maintainable code.
- Scalable architecture.
- Secure implementation.
- Consistent UI implementation.
- Proper testing.
- Clear technical documentation.

Do not behave like an AI that simply generates code without understanding the existing project.

Always inspect the project before making changes.

## 3. Technology Stack

### Backend
- Java 21
- Spring Boot 4.x
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL 8
- Flyway
- Bean Validation

### Frontend
- Next.js App Router
- React
- TypeScript
- Tailwind CSS
- shadcn/ui
- Lucide React

### Infrastructure
- Docker
- Docker Compose
- Git
- Environment Variables

Use compatible stable dependency versions. Do not upgrade dependencies unnecessarily.

## 4. Mandatory UI Design Rules

IMPORTANT: The user will provide UI screenshots, images, or design references.

These references are the SINGLE SOURCE OF TRUTH for frontend design.

You MUST follow the provided UI design as closely as possible.

### UI Rules

1. Inspect all files inside `docs/ui-reference/` before implementing frontend pages.
2. Reproduce the provided design instead of inventing a new design.
3. Do not redesign, simplify, or change the reference design without permission.
4. Preserve the original:
   - Layout
   - Sidebar structure
   - Navigation placement
   - Component positioning
   - Colors
   - Typography
   - Font sizes
   - Spacing
   - Padding and margins
   - Border radius
   - Shadows
   - Icons
   - Card dimensions
   - Tables
   - Buttons
   - Input fields
5. Maintain consistent design across all pages.
6. Reuse existing UI components.
7. Use responsive design while preserving the reference layout and visual hierarchy.
8. Use the correct icons where identifiable from the reference.
9. Do not introduce unnecessary animations.
10. Do not replace the reference UI with a generic dashboard template.

### Before Implementing UI

First:

- Inspect all provided screenshots.
- Identify shared layout components.
- Identify reusable components.
- Identify page-specific components.
- Identify colors, typography, spacing, and dimensions.
- Prepare a UI implementation plan.

Then implement the UI.

### Missing UI References

If a page does not have a provided design:

- Reuse the existing design system.
- Follow the visual language of the closest existing page.
- Ask the user before making major design decisions.

Never silently introduce a completely different design.

## 5. Architecture Rules

### Backend Architecture

Follow feature-based modular architecture.

Each business feature should organize its own:

- Controller
- Service
- Repository
- Entity
- DTO

Keep responsibilities separated.

Controller:
- Handles HTTP requests.
- Validates input.
- Returns appropriate responses.
- Must not contain business logic.

Service:
- Contains business logic.
- Coordinates repositories and integrations.

Repository:
- Handles database operations.

Entity:
- Represents database models.

DTO:
- Defines API request and response structures.

Configuration:
- Contains application configuration.

Common:
- Contains shared exceptions, responses, utilities, and configurations.

Avoid unnecessary abstraction and overly complicated designs.

### Frontend Architecture

Use Next.js App Router.

Organize code by feature.

- `app/`: Routes and page layouts.
- `components/ui/`: Reusable UI primitives.
- `components/layout/`: Sidebar, Header, and navigation.
- `features/`: Feature-specific components and logic.
- `hooks/`: Custom React hooks.
- `services/`: API communication.
- `types/`: TypeScript types.
- `lib/`: Shared utilities.
- `providers/`: React context providers.

Keep components small and reusable.

Avoid placing all application logic inside page components.

## 6. Core Business Requirements

### Telegram Integration

The application must:

- Receive messages through Telegram Webhook.
- Store original raw message text.
- Preserve message content without rewriting.
- Store Telegram User ID.
- Store Chat ID.
- Store Telegram Message ID.
- Store Telegram Update ID.
- Store message type.
- Store original message timestamp.
- Prevent duplicate Telegram updates.
- Validate Telegram webhook secret tokens.
- Restrict access to authorized Telegram users.

Initially prioritize text messages.

Design the architecture to support other Telegram message types in the future.

### Raw Message Processing

CRITICAL:

Do not use AI to process messages.

Do not automatically:

- Categorize messages.
- Summarize messages.
- Translate messages.
- Rewrite messages.
- Extract expenses.
- Generate tags.
- Change the original message.

Store the original text exactly as received.

### Message Management

Users should be able to:

- View messages.
- Search messages.
- Filter messages by date.
- Sort messages.
- View message details.
- Delete messages.
- Navigate through paginated results.

### Dashboard

Display:

- Total messages.
- Today's messages.
- Weekly messages.
- Monthly messages.
- Recent messages.
- Activity statistics.

## 7. Database Rules

Use MySQL and Flyway migrations.

Main tables:

- users
- telegram_messages

Requirements:

- Use appropriate primary keys.
- Use unique constraints for Telegram update IDs.
- Add useful indexes.
- Use proper foreign keys.
- Avoid duplicate records.
- Use appropriate timestamp types.
- Never store passwords as plain text.

Do not modify existing production database structures without explicit approval.

## 8. API Rules

Follow RESTful API conventions.

Use:

- Appropriate HTTP methods.
- Correct HTTP status codes.
- Request DTOs.
- Response DTOs.
- Input validation.
- Pagination.
- Global exception handling.
- Consistent API response structures.

Never expose database entities directly as public API responses.

## 9. Security Rules

Security is mandatory.

- Never hardcode credentials.
- Never expose Telegram Bot Tokens.
- Never commit `.env` files.
- Use environment variables.
- Use BCrypt for password hashing.
- Validate Telegram webhook secret tokens.
- Protect private API endpoints.
- Restrict access to user-owned data.
- Configure CORS explicitly.
- Prevent SQL injection.
- Never log sensitive information.

## 10. Coding Standards

### Java

- Follow Java naming conventions.
- Use constructor injection.
- Keep classes focused.
- Use meaningful method names.
- Avoid unnecessary static methods.
- Use DTOs for API boundaries.
- Handle exceptions properly.
- Add JavaDoc for complex business logic.

### TypeScript / React

- Enable strict TypeScript.
- Avoid `any` unless absolutely necessary.
- Use reusable components.
- Use meaningful component names.
- Keep business logic outside UI components when appropriate.
- Handle loading, error, empty, and success states.
- Avoid unnecessary client components.
- Follow Next.js App Router best practices.

### General

- Write readable code.
- Avoid duplicated logic.
- Avoid overengineering.
- Follow existing conventions.
- Do not create unnecessary files.
- Do not leave unfinished TODOs without explanation.

## 11. Testing Requirements

Backend:
- Unit tests.
- Service tests.
- Repository tests.
- Controller integration tests.
- Telegram webhook tests.
- Duplicate message prevention tests.
- Security tests.

Frontend:
- Component tests.
- Form validation tests.
- API integration tests.
- Loading and error state tests.

Always run relevant tests after implementation.

Never claim tests passed without executing them.

## 12. Development Workflow

Work in small, verifiable phases.

Phase 1: Project initialization.

Phase 2: Database configuration and migrations.

Phase 3: Backend architecture.

Phase 4: Telegram Bot integration.

Phase 5: Message REST APIs.

Phase 6: Authentication and security.

Phase 7: Frontend implementation based on provided UI references.

Phase 8: Backend and frontend integration.

Phase 9: Testing and bug fixing.

Phase 10: Docker and deployment.

Do not skip phases without user approval.

After each phase:

1. Summarize completed work.
2. List files created or modified.
3. Explain important implementation decisions.
4. Run relevant tests.
5. Show commands to run the application.
6. Identify remaining issues.
7. Wait for user approval before proceeding to the next major phase.

## 13. Git Rules

- Do not commit automatically.
- Do not push automatically.
- Do not force push.
- Do not delete branches without permission.
- Never commit secrets or credentials.
- Use clear and meaningful commit messages.
- Review changes before suggesting a commit.

## 14. Documentation

Maintain:

- README.md
- Architecture documentation.
- Database documentation.
- API documentation.
- Environment configuration examples.
- Local development instructions.
- Deployment instructions.

Keep documentation updated when implementation changes.

## 15. Final Instructions

Before writing code:

1. Read this CLAUDE.md.
2. Inspect the existing project.
3. Inspect UI references.
4. Understand the existing architecture.
5. Present an implementation plan.

During implementation:

- Follow all project rules.
- Follow the provided UI exactly.
- Prioritize security and maintainability.
- Test important functionality.
- Avoid unnecessary changes.

When requirements are unclear, ask the user instead of making important assumptions.

The most important priorities are:

1. Follow the provided UI design.
2. Preserve original Telegram messages.
3. Maintain clean architecture.
4. Keep the application secure.
5. Build a working, tested application.

You are responsible for delivering a maintainable, production-ready application, not just generating code.