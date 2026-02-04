package com.lefort.chat_service.service;

import com.lefort.chat_service.entities.ChatCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatEventProducer {

    private final KafkaTemplate<Long, ChatCreatedEvent> kafkaTemplate;

    public ChatEventProducer(KafkaTemplate<Long, ChatCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishChatCreated(ChatCreatedEvent event) {
        kafkaTemplate.send("chat.created", event.getChatId(), event);
        System.out.println("Event chat.created envoyé : " + event);
    }
}
