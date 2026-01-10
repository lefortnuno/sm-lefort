package com.lefort.user_service;

import com.lefort.user_service.entities.User;
import com.lefort.user_service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; 

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            User l1= User.builder()
                    .idUser("1")
                    .username("admin")   
                    .build();

            userRepository.save(l1);

            User l2=User.builder()
                    .idUser("2")
                    .username("root")   
                    .build();
            userRepository.save(l2);

        };
    }
}
