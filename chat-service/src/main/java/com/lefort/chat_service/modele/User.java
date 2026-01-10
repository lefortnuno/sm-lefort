package com.lefort.chat_service.modele;
 
import lombok.*; 
import jakarta.persistence.*; 

// @Table(name = "users")
@Setter @Getter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    private String idUser;
    private String username; 
}
