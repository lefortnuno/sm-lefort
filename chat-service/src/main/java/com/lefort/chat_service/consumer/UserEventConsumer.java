package com.lefort.chat_service.consumer;

import com.lefort.chat_service.entities.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    @KafkaListener(topics = "user.created", groupId = "chat-group")
    public void consume(UserCreatedEvent event) {
        System.out.println("User reçu : " + event);
        // push une notification au frontend
    }
}
