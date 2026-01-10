package com.lefort.ai_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
@Builder
public class Ai {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAi;
    private String libelle;
    private Boolean is_active; 
}
 