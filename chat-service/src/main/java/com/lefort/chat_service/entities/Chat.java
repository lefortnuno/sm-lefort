package com.lefort.chat_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime; 
import com.lefort.chat_service.modele.User;
import com.lefort.chat_service.modele.Ai;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString 
@Builder 

public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChat;
    private String chatcontent;
    private LocalDateTime created_at; 
    
    private String senderUserId;
    private String receiverUserId;
    private Long aiId;

    @Transient
    private User sender;

    @Transient
    private User receiver;
 
    @Transient
    private Ai ai;
    
    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}
