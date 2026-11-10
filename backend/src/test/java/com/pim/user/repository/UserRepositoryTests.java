package com.pim.user.repository;

import com.pim.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTests {

	/** A syntactically valid BCrypt hash; no real password is involved. */
	private static final String HASH = "$2a$10$abcdefghijklmnopqrstuuJ0yQe2bS3fV1uX9mY8lK7jH6gF5dE4";

	@Autowired
	private UserRepository userRepository;

	@Test
	void savesUserWithDefaultsAndTimestamps() {
		User saved = userRepository.saveAndFlush(new User("maya", HASH));

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.isEnabled()).isTrue();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();
	}

	@Test
	void findsUserByUsernameAndTelegramUserId() {
		User user = new User("maya", HASH);
		user.setTelegramUserId(123456789L);
		userRepository.saveAndFlush(user);

		assertThat(userRepository.findByUsername("maya")).isPresent();
		assertThat(userRepository.findByTelegramUserId(123456789L)).isPresent();
		assertThat(userRepository.findByTelegramUserId(1L)).isEmpty();
	}

	@Test
	void rejectsDuplicateUsernameIgnoringCase() {
		userRepository.saveAndFlush(new User("maya", HASH));

		assertThatThrownBy(() -> userRepository.saveAndFlush(new User("Maya", HASH)))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void rejectsTelegramUserLinkedToTwoUsers() {
		User first = new User("maya", HASH);
		first.setTelegramUserId(42L);
		userRepository.saveAndFlush(first);

		User second = new User("alex", HASH);
		second.setTelegramUserId(42L);

		assertThatThrownBy(() -> userRepository.saveAndFlush(second))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

}
