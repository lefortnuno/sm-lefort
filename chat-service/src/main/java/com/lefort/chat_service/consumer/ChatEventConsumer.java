package com.lefort.chat_service.consumer;

import com.lefort.chat_service.entities.ChatCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
 
import com.lefort.chat_service.entities.GrammarCorrectionRequest; 
import com.lefort.chat_service.entities.GrammarCorrectionResponse;
import org.springframework.kafka.core.KafkaTemplate; 
import org.springframework.beans.factory.annotation.Autowired;
 
import java.util.*; 
import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.repositories.ChatRepository;    
import org.springframework.transaction.annotation.Transactional;  

import java.time.LocalDateTime; 

import com.lefort.chat_service.entities.Chat; 
    

@Service 
public class ChatEventConsumer { 
    
    @Autowired
    private KafkaTemplate<Long, Object> kafkaTemplate;

    
    @Autowired
    private ChatRepository chatRepository;

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
                    chat.setUpdated_at(LocalDateTime.now());
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