-- V1: core schema for the Personal Information Manager.
-- All timestamps are stored in UTC (the JDBC connection is forced to UTC).

CREATE TABLE users (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    username         VARCHAR(50)  NOT NULL,
    -- BCrypt hash only; plain-text passwords are never stored.
    password_hash    VARCHAR(100) NOT NULL,
    display_name     VARCHAR(100) NULL,
    -- Telegram account authorised to send messages for this user.
    telegram_user_id BIGINT       NULL,
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_telegram_user_id UNIQUE (telegram_user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE telegram_messages (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    user_id             BIGINT      NOT NULL,
    telegram_update_id  BIGINT      NOT NULL,
    telegram_message_id BIGINT      NOT NULL,
    telegram_chat_id    BIGINT      NOT NULL,
    -- Sender ("from.id") as reported by Telegram.
    telegram_user_id    BIGINT      NOT NULL,
    message_type        VARCHAR(32) NOT NULL,
    -- Original message text exactly as received. NULL for non-text messages.
    -- Telegram limits text to 4096 characters, which fits in TEXT with utf8mb4.
    raw_text            TEXT        NULL,
    -- Original Telegram message timestamp ("date").
    message_date        DATETIME(6) NOT NULL,
    -- When this application received and stored the message.
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_telegram_messages PRIMARY KEY (id),
    -- Telegram retries webhook deliveries; one row per update.
    CONSTRAINT uk_telegram_messages_update_id UNIQUE (telegram_update_id),
    -- A message ID is unique within a chat.
    CONSTRAINT uk_telegram_messages_chat_message UNIQUE (telegram_chat_id, telegram_message_id),
    CONSTRAINT fk_telegram_messages_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Listing, sorting, date filtering and dashboard counts are all per user by date.
CREATE INDEX idx_telegram_messages_user_date ON telegram_messages (user_id, message_date);
