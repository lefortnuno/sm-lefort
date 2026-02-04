package com.lefort.chat_service.service;

import com.lefort.chat_service.entities.ChatCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.lefort.chat_service.entities.GrammarCorrectionRequest; 


@Service
public class ChatEventProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public ChatEventProducer(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishChatCreated(ChatCreatedEvent event) { 
        System.out.println("Chat recu dans chat-service : " + event);

        // Publier une demande de correction au LLM
        GrammarCorrectionRequest correctionRequest = new GrammarCorrectionRequest(
            event.getChatId(),
            event.getChatcontent()
        );

        kafkaTemplate.send("grammar.correction.request", correctionRequest);
        System.out.println("Demande de correction envoyée pour le chat ID: " + event.getChatId());
    }
}
