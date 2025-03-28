package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.MaxToken;
import io.muzoo.ssc.project.backend.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaxTokenRepository extends JpaRepository<MaxToken, Long> {
    MaxToken findFirstById(Long id);
    MaxToken findFirstByUser_IdAndAi_Id(Long userId, Long aiId);
}
