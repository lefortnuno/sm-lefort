package com.lefort.chatbot_service.modele;
 
import lombok.*; 
import jakarta.persistence.*; 

// @Table(name = "users")
@Setter @Getter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    private Long idUser;
    private String username; 
}
