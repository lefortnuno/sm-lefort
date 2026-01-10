package com.lefort.ai_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.lefort.ai_service.entities.Ai;
import com.lefort.ai_service.repositories.AiRepository;
import com.lefort.ai_service.web.AiController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner run(AiRepository aiRepository) {
        return args -> {
            Ai c1 = Ai
                    .builder()
                    .libelle("ollama")
                    .is_active(true) 
                    .build();
            aiRepository.save(c1);

            Ai c2 = Ai
                    .builder()
                    .libelle("qween")
                    .is_active(true) 
                    .build();
            aiRepository.save(c2); 
        };
    }

}

