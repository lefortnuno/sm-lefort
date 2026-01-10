package com.lefort.chatbot_service.modele;

import lombok.*; 

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Ai {
    private Long idAi;
    private String libelle; 
    private Boolean is_active;

}
 