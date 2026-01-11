package com.lefort.chat_service.consumer;

import com.lefort.common_events.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    @KafkaListener(topics = "user.created", groupId = "chat-group")
    public void consume(UserCreatedEvent event) {
        System.out.println("User recu dans chat-service : " + event);

        // TODO:
        // créer un profil chat
        // initialiser conversations
    }
}
