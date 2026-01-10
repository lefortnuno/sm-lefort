package com.lefort.ai_service.web;

import com.lefort.ai_service.entities.Ai;
import com.lefort.ai_service.repositories.AiRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AiController {
    AiRepository aiRepository;

    public  AiController(AiRepository aiRepository) {
        this.aiRepository = aiRepository;
    }

    @GetMapping("/ais")
    public List<Ai> getAllAis() {
        return aiRepository.findAll();
    }

    @GetMapping("/ais/{id}")
    public Ai getAiById(@PathVariable Long id) {
        return aiRepository.findById(id).get();
    }

    @PostMapping("/ais")
    public Ai createAi(@RequestBody Ai ai) {
        return aiRepository.save(ai);
    }

    @PutMapping("/ais/{id}")
    public Ai updateAi(@PathVariable Long id, @RequestBody Ai ai) {
        Ai p = aiRepository.findById(id).get();
        BeanUtils.copyProperties(ai, p);
        aiRepository.save(p);
        return p;
    }

    @DeleteMapping("/ais/{id}")
    public void deleteAi(@PathVariable Long id) {
        aiRepository.deleteById(id);
    }
}
