package com.lefort.chatbot_service.web;

import com.lefort.chatbot_service.modele.Ai;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("AI-SERVICE")
public interface AiOpenFeign {
    @GetMapping("/ais")
    public List<Ai> getAllAis();
    @GetMapping("/ais/{id}")
    public Ai getAiById(@PathVariable Long id);
}
