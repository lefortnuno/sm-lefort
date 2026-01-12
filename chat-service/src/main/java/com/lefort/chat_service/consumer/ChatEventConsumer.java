package com.lefort.chat_service.consumer;

import com.lefort.common_events.ChatCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
 
import com.lefort.common_events.GrammarCorrectionRequest; 
import com.lefort.common_events.GrammarCorrectionResponse;
import org.springframework.kafka.core.KafkaTemplate; 
import org.springframework.beans.factory.annotation.Autowired;
 
import java.util.*; 
import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.repositories.ChatRepository;    
import org.springframework.transaction.annotation.Transactional; 

@Service
public class ChatEventConsumer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private ChatRepository chatRepository;

    @KafkaListener(topics = "chat.created", groupId = "chat-group")
    public void consume(ChatCreatedEvent event) {
        System.out.println("Chat recu dans chat-service : " + event);

        // Publier une demande de correction
        GrammarCorrectionRequest correctionRequest = new GrammarCorrectionRequest(
            event.getChatId(),
            event.getChatcontent()
        );
        
        kafkaTemplate.send("grammar.correction.request", correctionRequest);
        System.out.println("Demande de correction envoyée pour le chat ID: " + event.getChatId());
    }
    
    @KafkaListener(topics = "grammar.correction.response", groupId = "chat-group")
    public void consumeResponse(GrammarCorrectionResponse response) {
        System.out.println("=== RÉSULTAT CORRECTION GRAMMATICALE ===");
        System.out.println("Chat ID: " + response.getChatId());
        System.out.println("Status: " + response.isStatus());
        System.out.println("Task: " + response.getTask());
        System.out.println("Texte original: [non affiché]");
        System.out.println("Texte corrigé: " + response.getCorrectedText());
        System.out.println("========================================");

        if (response.isStatus() && "SUCCESS".equals(response.getTask())) {
            Long chatId = response.getChatId();
            String correctedText = response.getCorrectedText();

            try {
                Optional<Chat> optionalChat = chatRepository.findById(chatId);
                
                if (optionalChat.isPresent()) {
                    Chat chat = optionalChat.get();
                    
                    // Sauvegarder l'ancien contenu pour log
                    String oldContent = chat.getChatcontent();
                    
                    // Mettre à jour avec le texte corrigé
                    chat.setChatmaj(true);
                    chat.setChatcontent(correctedText);
                    chatRepository.save(chat);
                    
                    System.out.println("Chat ID " + chatId + " mis à jour avec succès");
                    System.out.println(" Ancien texte: " + oldContent);
                    System.out.println(" Nouveau texte: " + correctedText);
                } else {
                    System.err.println("Chat ID " + chatId + " non trouvé dans la base de données");
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour du chat ID " + chatId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}