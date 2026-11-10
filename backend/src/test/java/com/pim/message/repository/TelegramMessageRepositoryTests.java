package com.pim.message.repository;

import com.pim.message.entity.MessageType;
import com.pim.message.entity.TelegramMessage;
import com.pim.user.entity.User;
import com.pim.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TelegramMessageRepositoryTests {

	/** A syntactically valid BCrypt hash; no real password is involved. */
	private static final String HASH = "$2a$10$abcdefghijklmnopqrstuuJ0yQe2bS3fV1uX9mY8lK7jH6gF5dE4";

	private static final Instant MESSAGE_DATE = Instant.parse("2026-01-18T03:04:05Z");

	@Autowired
	private TelegramMessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private User user;

	@BeforeEach
	void setUp() {
		user = userRepository.saveAndFlush(new User("maya", HASH));
	}

	@Test
	void preservesRawTextExactly() {
		String raw = "  Leading and trailing spaces  \r\n"
				+ "Tabs\tand\nnewlines\n\n"
				+ "한국어 메시지, 日本語, العربية\n"
				+ "Emoji 👋🏽 👨‍👩‍👧 🇰🇷 and symbols <>&\"'%_\\\n"
				+ "SELECT * FROM users; -- not executed";

		Long id = messageRepository.saveAndFlush(message(1L, 10L, raw)).getId();
		entityManager.clear();

		TelegramMessage loaded = messageRepository.findById(id).orElseThrow();
		assertThat(loaded.getRawText()).isEqualTo(raw);
		assertThat(loaded.getMessageDate()).isEqualTo(MESSAGE_DATE);
		assertThat(loaded.getMessageType()).isEqualTo(MessageType.TEXT);
		assertThat(loaded.getCreatedAt()).isNotNull();
	}

	@Test
	void storesMaximumLengthTelegramMessage() {
		// Telegram's limit is 4096 characters; use 4-byte characters for the worst case.
		String raw = "😀".repeat(4096);

		Long id = messageRepository.saveAndFlush(message(1L, 10L, raw)).getId();
		entityManager.clear();

		assertThat(messageRepository.findById(id).orElseThrow().getRawText()).isEqualTo(raw);
	}

	@Test
	void allowsMessagesWithoutText() {
		TelegramMessage photo = new TelegramMessage(user, 1L, 10L, 500L, 900L, MessageType.PHOTO, null, MESSAGE_DATE);

		Long id = messageRepository.saveAndFlush(photo).getId();
		entityManager.clear();

		assertThat(messageRepository.findById(id).orElseThrow().getRawText()).isNull();
	}

	@Test
	void rejectsDuplicateTelegramUpdateId() {
		messageRepository.saveAndFlush(message(1L, 10L, "first"));

		assertThatThrownBy(() -> messageRepository.saveAndFlush(message(1L, 11L, "retry")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void rejectsDuplicateMessageIdInSameChat() {
		messageRepository.saveAndFlush(message(1L, 10L, "first"));

		assertThatThrownBy(() -> messageRepository.saveAndFlush(message(2L, 10L, "same message")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void reportsWhetherUpdateWasAlreadyStored() {
		messageRepository.saveAndFlush(message(1L, 10L, "hello"));

		assertThat(messageRepository.existsByTelegramUpdateId(1L)).isTrue();
		assertThat(messageRepository.existsByTelegramUpdateId(2L)).isFalse();
	}

	@Test
	void deletingUserDeletesTheirMessages() {
		messageRepository.saveAndFlush(message(1L, 10L, "hello"));
		entityManager.clear();

		userRepository.deleteById(user.getId());
		userRepository.flush();

		Integer remaining = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM telegram_messages", Integer.class);
		assertThat(remaining).isZero();
	}

	@Test
	void createsUserDateIndex() {
		Integer indexColumns = jdbcTemplate.queryForObject("""
				SELECT COUNT(*) FROM information_schema.statistics
				WHERE table_schema = DATABASE()
				  AND table_name = 'telegram_messages'
				  AND index_name = 'idx_telegram_messages_user_date'
				""", Integer.class);
		assertThat(indexColumns).isEqualTo(2);
	}

	private TelegramMessage message(Long updateId, Long messageId, String text) {
		return new TelegramMessage(user, updateId, messageId, 500L, 900L, MessageType.TEXT, text, MESSAGE_DATE);
	}

}
