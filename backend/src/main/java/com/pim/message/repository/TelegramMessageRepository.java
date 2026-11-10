package com.pim.message.repository;

import com.pim.message.entity.TelegramMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramMessageRepository extends JpaRepository<TelegramMessage, Long> {

	boolean existsByTelegramUpdateId(Long telegramUpdateId);

}
