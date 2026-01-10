package com.lefort.user_service.web;

import com.lefort.user_service.entities.User;
import com.lefort.user_service.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class UserController {

    UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User findById(@PathVariable String id) {
        return   userRepository.findById(id).get();

    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    
    @PostMapping("/users/ensure")
    public User ensureUser(@RequestBody User user) {
        return userRepository
            .findById(user.getIdUser())
            .orElseGet(() -> userRepository.save(user));
    }
}
