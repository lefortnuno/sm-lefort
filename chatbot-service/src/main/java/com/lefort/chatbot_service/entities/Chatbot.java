package com.lefort.chatbot_service.entities;

import java.time.LocalDateTime;
import com.lefort.chatbot_service.modele.User;
import com.lefort.chatbot_service.modele.Ai;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@Builder
public class Chatbot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChatbot;
    private String chatbotcontent;
    private LocalDateTime created_at;
    private Boolean the_sender;

    private Long idAi;
    @Transient
    private Ai ai;

    private Long idUser;
    @Transient
    private User user;
    
    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}
 