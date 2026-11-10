package com.pim.message.entity;

import com.pim.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * A message received from Telegram, stored exactly as it arrived.
 *
 * <p>Every column is {@code updatable = false} and there are no setters: once a
 * message is stored it is never modified, only read or deleted.
 */
@Entity
@Table(name = "telegram_messages")
public class TelegramMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@Column(name = "telegram_update_id", nullable = false, updatable = false)
	private Long telegramUpdateId;

	@Column(name = "telegram_message_id", nullable = false, updatable = false)
	private Long telegramMessageId;

	@Column(name = "telegram_chat_id", nullable = false, updatable = false)
	private Long telegramChatId;

	@Column(name = "telegram_user_id", nullable = false, updatable = false)
	private Long telegramUserId;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_type", nullable = false, length = 32, updatable = false)
	private MessageType messageType;

	/** Original text exactly as received; {@code null} for non-text messages. */
	@Column(name = "raw_text", columnDefinition = "TEXT", updatable = false)
	private String rawText;

	/** Original Telegram message timestamp. */
	@Column(name = "message_date", nullable = false, updatable = false)
	private Instant messageDate;

	/** When this application stored the message. */
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected TelegramMessage() {
		// Required by JPA.
	}

	public TelegramMessage(User user, Long telegramUpdateId, Long telegramMessageId, Long telegramChatId,
			Long telegramUserId, MessageType messageType, String rawText, Instant messageDate) {
		this.user = user;
		this.telegramUpdateId = telegramUpdateId;
		this.telegramMessageId = telegramMessageId;
		this.telegramChatId = telegramChatId;
		this.telegramUserId = telegramUserId;
		this.messageType = messageType;
		this.rawText = rawText;
		this.messageDate = messageDate;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Long getTelegramUpdateId() {
		return telegramUpdateId;
	}

	public Long getTelegramMessageId() {
		return telegramMessageId;
	}

	public Long getTelegramChatId() {
		return telegramChatId;
	}

	public Long getTelegramUserId() {
		return telegramUserId;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getRawText() {
		return rawText;
	}

	public Instant getMessageDate() {
		return messageDate;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

}
