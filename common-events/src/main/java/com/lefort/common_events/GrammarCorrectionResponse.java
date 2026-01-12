package com.lefort.common_events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrammarCorrectionResponse {
    private Long chatId;
    private boolean status;
    private String correctedText;
    private String task; 
}