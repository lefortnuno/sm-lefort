package com.lefort.user_service.entities;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime; 

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor @NoArgsConstructor @Builder

@Table(name = "users")
public class User {
    @Id  
    private String idUser;  
    private String username;
    private LocalDateTime created_at; 
    
    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}

