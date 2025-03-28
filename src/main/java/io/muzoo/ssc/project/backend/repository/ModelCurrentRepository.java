package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.ModelCurrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelCurrentRepository extends JpaRepository<ModelCurrent, Long> {
    ModelCurrent findFirstById(Long id);
    ModelCurrent findFirstByUser_IdAndAi_Id(Long userId, Long aiId);
}
