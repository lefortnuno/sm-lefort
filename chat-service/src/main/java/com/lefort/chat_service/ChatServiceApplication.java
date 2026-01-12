package com.lefort.chat_service;


import com.lefort.chat_service.entities.Chat;
import com.lefort.chat_service.repositories.ChatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean; 

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

    
    @Bean
    CommandLineRunner run(ChatRepository chatRepository) {
        return args -> {
            // Chat chat11 = Chat
            //         .builder()
            //         .senderUserId("1")
            //         .receiverUserId("2")
            //         .aiId(1L) 
            //         .chatcontent("Bonjour le monde !") 
            //         .build();
            // chatRepository.save(chat11);
            // Chat chat12 = Chat
            //         .builder() 
            //         .senderUserId("2")
            //         .receiverUserId("1")
            //         .aiId(1L) 
            //         .chatcontent("RE-Bonjour le monde !") 
            //         .build();
            // chatRepository.save(chat12); 
 
        };
    }
}
