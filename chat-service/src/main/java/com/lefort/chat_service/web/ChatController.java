package com.lefort.chat_service.web;

import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.repositories.ChatRepository;
import com.lefort.chat_service.modele.User;
import com.lefort.chat_service.modele.Ai;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.List;

@RestController
public class ChatController {

    private final ChatRepository chatRepository;
    private final UserOpenFeign userOpenFeign;
    private final AiOpenFeign aiOpenFeign;

    public ChatController(
            ChatRepository chatRepository,
            UserOpenFeign userOpenFeign,
            AiOpenFeign aiOpenFeign
    ) {
        this.chatRepository = chatRepository;
        this.userOpenFeign = userOpenFeign;
        this.aiOpenFeign = aiOpenFeign;
    }

    // ======================== GET ALL ========================
    @GetMapping("/chats")
    public List<Chat> findAll() {

        List<Chat> chats = chatRepository.findAll();
        List<User> users = userOpenFeign.getAllUsers();
        List<Ai> ais = aiOpenFeign.getAllAis();

        for (Chat chat : chats) {

            for (User user : users) {
                if (chat.getSenderUserId() != null &&
                        chat.getSenderUserId().equals(user.getIdUser())) {
                    chat.setSender(user);
                }

                if (chat.getReceiverUserId() != null &&
                        chat.getReceiverUserId().equals(user.getIdUser())) {
                    chat.setReceiver(user);
                }
            }

            for (Ai ai : ais) {
                if (chat.getAiId() != null &&
                        chat.getAiId().equals(ai.getIdAi())) {
                    chat.setAi(ai);
                }
            }
        }
        return chats;
    }

    // ======================== GET BY ID ========================
    @GetMapping("/chats/{id}")
    public Chat findById(@PathVariable Long id) {

        Chat chat = chatRepository.findById(id).orElseThrow();

        User sender = userOpenFeign.getUserById(chat.getSenderUserId());
        User receiver = userOpenFeign.getUserById(chat.getReceiverUserId());
        Ai ai = aiOpenFeign.getAiById(chat.getAiId());

        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setAi(ai);

        return chat;
    }

    // ======================== CONVERSATION ========================
    @GetMapping("/conv")
    public List<Chat> findConversation(
            @RequestParam String u1,
            @RequestParam String u2
    ) {

        List<Chat> chats =
                chatRepository
                        .findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(
                                u1, u2,
                                u2, u1
                        );

        List<User> users = userOpenFeign.getAllUsers();
        List<Ai> ais = aiOpenFeign.getAllAis();

        for (Chat chat : chats) {

            for (User user : users) {
                if (chat.getSenderUserId() != null &&
                        chat.getSenderUserId().equals(user.getIdUser())) {
                    chat.setSender(user);
                }

                if (chat.getReceiverUserId() != null &&
                        chat.getReceiverUserId().equals(user.getIdUser())) {
                    chat.setReceiver(user);
                }
            }

            for (Ai ai : ais) {
                if (chat.getAiId() != null &&
                        chat.getAiId().equals(ai.getIdAi())) {
                    chat.setAi(ai);
                }
            }
        }
        return chats;
    }

    // ======================== SAVE ========================
    @PostMapping("/chats")
    public Chat save(@RequestBody Chat chat) {
        return chatRepository.save(chat);
    }

    // ======================== UPDATE ========================
    @PutMapping("/chats/{id}")
    public Chat update(@PathVariable Long id, @RequestBody Chat chat) {
        Chat existing = chatRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(chat, existing);
        return chatRepository.save(existing);
    }

    // ======================== DELETE ========================
    @DeleteMapping("/chats/{id}")
    public void deleteById(@PathVariable Long id) {
        chatRepository.deleteById(id);
    }
}
