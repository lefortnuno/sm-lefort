package com.lefort.ai_service.consumer;

import com.lefort.common_events.GrammarCorrectionRequest;
import com.lefort.common_events.GrammarCorrectionResponse;
import com.lefort.ai_service.service.ServiceLLM;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Service
public class AiEventConsumer {

    @Autowired
    private ServiceLLM serviceLLM;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @KafkaListener(topics = "grammar.correction.request", groupId = "ai-group")
    public void processCorrectionRequest(GrammarCorrectionRequest request) {
        System.out.println("Demande de correction reçue pour chat ID: " + request.getChatId());
        
        // Traitement avec le service LLM
        Map<String, Object> result = serviceLLM.corrigerGrammaire(request.getOriginalText());
        
        // Publier la réponse
        GrammarCorrectionResponse response = new GrammarCorrectionResponse(
            request.getChatId(),
            (Boolean) result.get("status"),
            (String) result.get("response"),
            (String) result.get("task")
        );
        
        kafkaTemplate.send("grammar.correction.response", response);
        System.out.println("Réponse de correction envoyée pour chat ID: " + request.getChatId());
    }
}