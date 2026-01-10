package com.lefort.chatbot_service.repositories;

import com.lefort.chatbot_service.entities.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {
}
