package com.lefort.chat_service.web;

import com.lefort.chat_service.modele.Ai;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("AI-SERVICE")
public interface AiOpenFeign {
    @GetMapping("/ais")
    public List<Ai> getAllAis();
    @GetMapping("/ais/{id}")
    public Ai getAiById(@PathVariable("id") Long id);
}
