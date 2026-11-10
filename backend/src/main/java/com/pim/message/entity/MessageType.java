package com.pim.message.entity;

/**
 * Kind of Telegram message. Only {@link #TEXT} is processed initially; the
 * other values let the schema hold additional types without a migration.
 */
public enum MessageType {
	TEXT,
	PHOTO,
	VIDEO,
	DOCUMENT,
	AUDIO,
	VOICE,
	STICKER,
	OTHER
}
