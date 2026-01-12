package com.lefort.ai_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import org.springframework.beans.factory.annotation.*; 

@Service
public class ServiceLLM {

    private static String GROQ_API_URL;
    private static String API_KEY;
    private static String MODEL;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
 
    @Autowired
    public ServiceLLM(@Value("${groq.api.url}") String apiUrl,
                     @Value("${groq.api.key}") String apiKey,
                     @Value("${groq.api.model}") String model) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
         
        GROQ_API_URL = apiUrl;
        API_KEY = apiKey;
        MODEL = model;
    }

    public Map<String, Object> corrigerGrammaire(String chatContent) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);
            
            // Construire le prompt système STRICT pour la correction grammaticale
            String systemPrompt = 
                "Tu es un correcteur grammatical automatique. Tu dois:\n" +
                "1. Analyser le texte fourni\n" +
                "2. Corriger TOUTES les fautes de grammaire, orthographe et syntaxe\n" +
                "3. Retourner UNIQUEMENT un objet JSON valide au format suivant:\n" +
                "{\n" +
                "  \"status\": boolean,\n" +
                "  \"response\": string\n" +
                "}\n" +
                "\n" +
                "Règles:\n" +
                "- \"status\": true si tu as modifié au moins une faute, false si le texte était déjà parfait\n" +
                "- \"response\": contient le texte corrigé (même s'il n'a pas changé)\n" +
                "- Ne jamais inclure de texte en dehors du JSON\n" +
                "- Ne jamais utiliser de balises comme <think> ou <output>\n" +
                "- Le JSON doit être parseable directement\n" +
                "- Conserve le sens, le ton et le style original\n" +
                "- Travaille sur toutes les langues";
            
            // Construire le prompt utilisateur avec instructions claires
            String userPrompt = 
                "Corrige grammaticalement ce texte et retourne UNIQUEMENT le JSON demandé:\n" +
                "\"" + chatContent + "\"\n" +
                "\n" +
                "Format de sortie OBLIGATOIRE (rien d'autre):\n" +
                "{\n" +
                "  \"status\": true|false,\n" +
                "  \"response\": \"texte corrigé ici\"\n" +
                "}";
            
            // Construire le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userPrompt));
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.1);
            requestBody.put("max_tokens", 1000);

            // Important: Forcer le format JSON si supporté
            requestBody.put("response_format", Map.of("type", "json_object"));
            
            // Envoyer la requête à l'API Groq
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    GROQ_API_URL, 
                    HttpMethod.POST, 
                    entity, 
                    Map.class
            );
            
            // Extraire la réponse du LLM
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String llmResponse = (String) message.get("content");
                    
                    // Nettoyer la réponse
                    llmResponse = llmResponse.trim();
                    
                    try {
                        // Essayer de parser la réponse comme JSON
                        Map<String, Object> parsedResponse = objectMapper.readValue(
                            llmResponse, 
                            Map.class
                        );
                        
                        // Extraire les valeurs
                        Boolean status = (Boolean) parsedResponse.get("status");
                        String correctedText = (String) parsedResponse.get("response");
                        
                        // Déterminer si des modifications ont été apportées
                        boolean hasChanges = status != null ? status : !chatContent.equals(correctedText);
                        
                        // Construire le résultat final
                        result.put("task", "SUCCESS");
                        result.put("status", hasChanges);
                        result.put("response", correctedText);
                        
                    } catch (Exception jsonError) {
                        // Si le parsing JSON échoue, utiliser l'ancienne méthode
                        System.err.println("Erreur de parsing JSON: " + jsonError.getMessage());
                        
                        // Essayer d'extraire du JSON de la réponse
                        String correctedText = extractJsonFromResponse(llmResponse);
                        if (correctedText == null) {
                            correctedText = llmResponse.replaceAll("<think>.*?</think>", "").trim();
                        }
                        
                        boolean hasChanges = !chatContent.equals(correctedText);
                        
                        result.put("task", "SUCCESS");
                        result.put("status", hasChanges);
                        result.put("response", correctedText);
                    }
                    
                    return result;
                }
            }
            
            // En cas d'échec d'extraction
            result.put("task", "FAILED");
            result.put("status", false);
            result.put("response", chatContent);
            
        } catch (Exception e) {
            // En cas d'erreur, retourner le texte original
            System.err.println("Erreur lors de l'appel à l'API Groq: " + e.getMessage());
            e.printStackTrace();
            result.put("task", "FAILED");
            result.put("status", false);
            result.put("response", chatContent);
        }
        
        return result;
    }
    
    
    private String extractJsonFromResponse(String response) {
        try {
            // Chercher le premier { et le dernier }
            int start = response.indexOf('{');
            int end = response.lastIndexOf('}');
            
            if (start != -1 && end != -1 && end > start) {
                String jsonStr = response.substring(start, end + 1);
                Map<String, Object> parsed = objectMapper.readValue(jsonStr, Map.class);
                return (String) parsed.get("response");
            }
        } catch (Exception e) {
            // Ignorer et retourner null
        }
        return null;
    }
}