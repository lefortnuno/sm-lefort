package com.lefort.chatbot_service.web;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.lefort.chatbot_service.entities.Chatbot;
import com.lefort.chatbot_service.modele.User; 
import com.lefort.chatbot_service.modele.Ai;
import com.lefort.chatbot_service.repositories.ChatbotRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatbotController {
    ChatbotRepository gardeRepository;
    UserOpenFeign studentOpenFeign;
    AiOpenFeign aiOpenFeign;

    public ChatbotController(ChatbotRepository gardeRepository,  UserOpenFeign studentOpenFeign, AiOpenFeign aiOpenFeign) {
        this.gardeRepository = gardeRepository;
        this.studentOpenFeign = studentOpenFeign;
        this.aiOpenFeign = aiOpenFeign;
    }


    @GetMapping("/chatbots")
    public List<Chatbot> findAll() {
        List<Chatbot> chatbots = gardeRepository.findAll();
        List<User> students = studentOpenFeign.getAllUsers();
        List<Ai> ais = aiOpenFeign.getAllAis();
        for(Chatbot garde : chatbots){
            for(User student : students){
                if (garde.getIdUser().equals(student.getIdUser())){
                    garde.setUser(student);
                    break;
                }
            }
            for (Ai ai : ais){
                if (garde.getIdAi().equals(ai.getIdAi())){
                    garde.setAi(ai);
                }
            }
        }
        return chatbots;
    }

    @GetMapping("/chatbots/{id}")
    public Chatbot findById(@PathVariable Long id) {
        Chatbot garde = gardeRepository.findById(id).get();
        User student = studentOpenFeign.getUserById(garde.getIdUser());
        Ai ai = aiOpenFeign.getAiById(garde.getIdChatbot());

        garde.setUser(student);
        garde.setAi(ai);
        return garde;
    }

    @PostMapping("/chatbots")
    public Chatbot save(@RequestBody Chatbot garde) {
        return gardeRepository.save(garde);
    }

    @PutMapping("/chatbots/{id}")
    public Chatbot update(@PathVariable Long id, @RequestBody Chatbot garde) {
        Chatbot cmd = gardeRepository.findById(id).get();
        BeanUtils.copyProperties(garde, cmd);
        gardeRepository.save(cmd);
        return cmd;
    }

    @DeleteMapping("/chatbots/{id}")
    public void deleteById(@PathVariable Long id) {
        gardeRepository.deleteById(id);
    }

}
