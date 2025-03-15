package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findFirstById(Long id);
    Chat findFirstByUser_IdAndAi_Id(Long userId, Long aiId);
}
