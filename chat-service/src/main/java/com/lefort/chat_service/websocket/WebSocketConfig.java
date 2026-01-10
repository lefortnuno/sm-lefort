package com.lefort.chat_service.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    // ==================== WebSocket Handler ====================
    @Bean
    public WebSocketHandler chatWebSocketHandler() {
        return session -> session.send(
                session.receive()
                        .map(msg -> session.textMessage("Echo: " + msg.getPayloadAsText()))
        );
    }

    // ==================== Adapter ====================
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    // ==================== Mapping ====================
    @Bean
    public SimpleUrlHandlerMapping webSocketMapping(WebSocketHandler chatWebSocketHandler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Map.of("/ws/chat", chatWebSocketHandler));
        mapping.setOrder(-1); // priorité haute
        return mapping;
    }
}
