package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChat_Id(Long chatId);
}
