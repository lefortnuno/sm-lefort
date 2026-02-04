package com.lefort.chat_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrammarCorrectionRequest {
    private Long chatId;
    private String originalText; 
}