package com.lefort.chat_service.service;

import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.repositories.ChatRepository;
import com.lefort.chat_service.modele.User;
import com.lefort.chat_service.modele.Ai;
import com.lefort.chat_service.web.UserOpenFeign;
import com.lefort.chat_service.web.AiOpenFeign;
import com.lefort.common_events.ChatCreatedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatEventProducer chatEventProducer;
    private final UserOpenFeign userOpenFeign;
    private final AiOpenFeign aiOpenFeign;

    public ChatService(ChatRepository chatRepository,
                       ChatEventProducer chatEventProducer,
                       UserOpenFeign userOpenFeign,
                       AiOpenFeign aiOpenFeign) {
        this.chatRepository = chatRepository;
        this.chatEventProducer = chatEventProducer;
        this.userOpenFeign = userOpenFeign;
        this.aiOpenFeign = aiOpenFeign;
    }

    // 🔹 GET ALL
    public List<Chat> getAllChats() {
        List<Chat> chats = chatRepository.findAll();
        return enrichChats(chats);
    }

    // 🔹 GET BY ID
    public Chat getChatById(Long id) {
        Chat chat = chatRepository.findById(id).orElseThrow();
        return enrichChat(chat);
    }

    // 🔹 GET CONVERSATION
    public List<Chat> getConversation(String u1, String u2) {
        List<Chat> chats =
                chatRepository.findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(
                        u1, u2, u2, u1
                );
        return enrichChats(chats);
    }

    // 🔹 CREATE + Kafka event
    public Chat createChat(Chat chat) {

        Chat savedChat = chatRepository.save(chat);

        chatEventProducer.publishChatCreated(
                new ChatCreatedEvent(
                        savedChat.getIdChat(),
                        savedChat.getSenderUserId(),
                        savedChat.getReceiverUserId(),
                        savedChat.getChatcontent()
                )
        );

        return enrichChat(savedChat);
    }

    // 🔹 UPDATE
    public Chat updateChat(Long id, Chat chat) {
        Chat existing = chatRepository.findById(id).orElseThrow();

        existing.setChatcontent(chat.getChatcontent());
        existing.setSenderUserId(chat.getSenderUserId());
        existing.setReceiverUserId(chat.getReceiverUserId());
        existing.setAiId(chat.getAiId());

        chatRepository.save(existing);

        return enrichChat(existing);
    }

    // 🔹 DELETE
    public void deleteChat(Long id) {
        chatRepository.deleteById(id);
    }

    private Chat enrichChat(Chat chat) {
        if (chat.getSenderUserId() != null) {
            User sender = userOpenFeign.getUserById(chat.getSenderUserId());
            chat.setSender(sender);
        }

        if (chat.getReceiverUserId() != null) {
            User receiver = userOpenFeign.getUserById(chat.getReceiverUserId());
            chat.setReceiver(receiver);
        }

        if (chat.getAiId() != null) {
            Ai ai = aiOpenFeign.getAiById(chat.getAiId());
            chat.setAi(ai);
        }

        return chat;
    }

    private List<Chat> enrichChats(List<Chat> chats) {
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
}
