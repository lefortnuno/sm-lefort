package com.lefort.ai_service.web;

import com.lefort.ai_service.entities.Ai;
import com.lefort.ai_service.service.ServiceLLM;
import com.lefort.ai_service.repositories.AiRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
public class AiController {
    AiRepository aiRepository;
    @Autowired
    private ServiceLLM serviceLLM;

    public  AiController(AiRepository aiRepository) {
        this.aiRepository = aiRepository;
    }

    @GetMapping("/ais")
    public List<Ai> getAllAis() {
        return aiRepository.findAll();
    }

    @GetMapping("/ais/{id}")
    public Ai getAiById(@PathVariable("id") Long id) {
        return aiRepository.findById(id).orElseThrow();
    }

    @PostMapping("/ais")
    public Ai createAi(@RequestBody Ai ai) {
        return aiRepository.save(ai);
    }

    @PutMapping("/ais/{id}")
    public Ai updateAi(@PathVariable("id") Long id, @RequestBody Ai ai) {
        Ai p = aiRepository.findById(id).get();
        BeanUtils.copyProperties(ai, p);
        aiRepository.save(p);
        return p;
    }

    @DeleteMapping("/ais/{id}")
    public void deleteAi(@PathVariable("id") Long id) {
        aiRepository.deleteById(id);
    }

    @PostMapping("/ais/correct")
    public Map<String, Object> correctGrammar(@RequestBody Map<String, String> request) {
        String chatContent = request.get("chatcontent");
        return serviceLLM.corrigerGrammaire(chatContent);
    }
}
