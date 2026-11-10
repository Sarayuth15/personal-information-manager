# Database

MySQL 8 (8.0 or newer), with the schema managed by Flyway. Migrations live in `backend/src/main/resources/db/migration/` and run automatically when the backend starts. Hibernate only **validates** the schema (`ddl-auto: validate`) and never changes it.

- **Character set:** `utf8mb4` everywhere, so emoji and all other Unicode are stored unchanged.
- **Time zone:** every `DATETIME(6)` value is UTC. The JDBC connection is forced to UTC (`connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true`), and Hibernate uses `jdbc.time_zone=UTC`. Java code uses `Instant`.

## Setting up MySQL

Run this once on your MySQL server as an admin user. Replace the password, and narrow the `'%'` host to the address the backend connects from if you can.

```sql
CREATE DATABASE pim      CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE pim_test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

CREATE USER 'pim'@'%' IDENTIFIED BY 'choose-a-strong-password';
GRANT ALL PRIVILEGES ON pim.*      TO 'pim'@'%';
GRANT ALL PRIVILEGES ON pim_test.* TO 'pim'@'%';
```

Flyway creates the tables the first time the backend or the tests connect.

## `users`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGINT` PK, auto-increment | |
| `username` | `VARCHAR(50)` NOT NULL | Unique and case-insensitive (`Maya` and `maya` count as the same name) |
| `password_hash` | `VARCHAR(100)` NOT NULL | BCrypt hash only. Never plain text. |
| `display_name` | `VARCHAR(100)` NULL | Name shown in the UI greeting |
| `telegram_user_id` | `BIGINT` NULL | Unique. Links one Telegram account to this user and is used to authorise webhook messages. |
| `enabled` | `BOOLEAN` NOT NULL, default `TRUE` | |
| `created_at` / `updated_at` | `DATETIME(6)` NOT NULL | Set automatically |

## `telegram_messages`

Messages are stored exactly as received and are **never updated**. The entity has no setters and every column is `updatable = false`. They can only be read or deleted.

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGINT` PK, auto-increment | |
| `user_id` | `BIGINT` NOT NULL | FK to `users.id`, `ON DELETE CASCADE` |
| `telegram_update_id` | `BIGINT` NOT NULL | **Unique.** Stops duplicate webhook deliveries from being stored twice. |
| `telegram_message_id` | `BIGINT` NOT NULL | Unique together with `telegram_chat_id` |
| `telegram_chat_id` | `BIGINT` NOT NULL | |
| `telegram_user_id` | `BIGINT` NOT NULL | Sender (`from.id`) |
| `message_type` | `VARCHAR(32)` NOT NULL | `TEXT`, `PHOTO`, `VIDEO`, `DOCUMENT`, `AUDIO`, `VOICE`, `STICKER` or `OTHER`. Only `TEXT` is processed for now. |
| `raw_text` | `TEXT` NULL | Original text, unchanged. NULL for messages without text. Fits Telegram's 4096-character limit. |
| `message_date` | `DATETIME(6)` NOT NULL | Original Telegram timestamp |
| `created_at` | `DATETIME(6)` NOT NULL | When this application stored the message |

### Constraints and indexes

| Name | Columns | Purpose |
|---|---|---|
| `uk_telegram_messages_update_id` | `telegram_update_id` | Prevents duplicates |
| `uk_telegram_messages_chat_message` | `telegram_chat_id, telegram_message_id` | Prevents duplicates |
| `idx_telegram_messages_user_date` | `user_id, message_date` | Listing, sorting, date filters and dashboard counts |
| `fk_telegram_messages_user` | `user_id` | Ownership. Deleting a user deletes their messages. |

## Changing the schema

Add a new file `V<n>__<description>.sql`. Never edit a migration that has already been applied: Flyway checksums would fail, and other databases would end up with a different schema.
