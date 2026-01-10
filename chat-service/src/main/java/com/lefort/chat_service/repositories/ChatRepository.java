package com.lefort.chat_service.repositories;

import com.lefort.chat_service.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ChatRepository  extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderUserIdAndReceiverUserId(String senderId, String receiverId);

    List<Chat> findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(
            String senderId1, String receiverId1,
            String senderId2, String receiverId2
    );
}
