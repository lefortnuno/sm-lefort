package com.lefort.ai_service.repositories;

import com.lefort.ai_service.entities.Ai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<Ai, Long> {
}
