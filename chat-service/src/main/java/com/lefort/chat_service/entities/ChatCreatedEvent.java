package com.lefort.chat_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreatedEvent {
    private Long chatId;
    private String senderUserId;
    private String receiverUserId;
    private String chatcontent;
}
