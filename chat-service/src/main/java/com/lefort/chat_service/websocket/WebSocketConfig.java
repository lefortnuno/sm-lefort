package com.lefort.chat_service.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/ws/chat")
                .setAllowedOrigins("*"); // Autoriser toutes les origines pour le test
    }

    // Handler interne pour gérer les messages
    class ChatWebSocketHandler extends TextWebSocketHandler {
        
        // Liste thread-safe pour stocker toutes les sessions actives
        private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            sessions.remove(session);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            // Broadcast : Envoyer le message à TOUTES les sessions connectées
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    // On renvoie le payload brut (le texte du message)
                    webSocketSession.sendMessage(message);
                }
            }
        }
    }
}
