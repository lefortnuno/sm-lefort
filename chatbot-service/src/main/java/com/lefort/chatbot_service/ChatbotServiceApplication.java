package com.lefort.chatbot_service;

import com.lefort.chatbot_service.entities.Chatbot;
import com.lefort.chatbot_service.repositories.ChatbotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean; 

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ChatbotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner run(ChatbotRepository chatbotRepository) {
        return args -> {
            Chatbot chatbot11 = Chatbot
                    .builder()
                    .idUser(1L)
                    .idAi(1L)  
                    .chatbotcontent("Teste d'api llm")
                    .the_sender(true)
                    .build();
            chatbotRepository.save(chatbot11);
            Chatbot chatbot12 = Chatbot
                    .builder()
                    .idUser(1L)
                    .idAi(1L)  
                    .chatbotcontent("Reponse d'api llm")
                    .the_sender(false)
                    .build();
            chatbotRepository.save(chatbot12);
            Chatbot chatbot13 = Chatbot
                    .builder()
                    .idUser(1L)
                    .idAi(1L)  
                    .chatbotcontent("Merci")
                    .the_sender(true)
                    .build();
            chatbotRepository.save(chatbot13);
 
        };
    }
}
