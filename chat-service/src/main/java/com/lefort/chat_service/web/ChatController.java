package com.lefort.chat_service.web;

import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.service.ChatService; 
import org.springframework.web.bind.annotation.*; 
import java.util.*;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService; 

    public ChatController(ChatService chatService) {
        this.chatService = chatService; 
    }  

    // ======================== GET ALL ========================
    @GetMapping
    public List<Chat> findAll() {
        return chatService.getAllChats();
    }

    // ======================== GET BY ID ========================
    @GetMapping("/{id}")
    public Chat findById(@PathVariable("id") Long id) {
        return chatService.getChatById(id);
    }

    // ======================== CONVERSATION ========================
    @GetMapping("/conv")
    public List<Chat> findConversation(
            @RequestParam("u1") String u1,
            @RequestParam("u2") String u2
    ) {
        return chatService.getConversation(u1, u2);
    }

    // ======================== SAVE ========================
    @PostMapping
    public Chat save(@RequestBody Chat chat) {
        return chatService.createChat(chat);
    }

    // ======================== UPDATE ========================
    @PutMapping("/{id}")
    public Chat update(@PathVariable("id") Long id, @RequestBody Chat chat) {
        return chatService.updateChat(id, chat);
    }

    // ======================== DELETE ========================
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        chatService.deleteChat(id);
    }
}
