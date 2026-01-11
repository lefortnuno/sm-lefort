package com.lefort.chat_service.consumer;

import com.lefort.common_events.ChatCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatEventConsumer {

    @KafkaListener(topics = "chat.created", groupId = "chat-group")
    public void consume(ChatCreatedEvent event) {
        System.out.println("Chat recu dans chat-service : " + event);

        // TODO:
        // créer un profil chat
        // initialiser conversations
    }
}
