package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.ModelAvailable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelAvailableRepository extends JpaRepository<ModelAvailable, Long> {
    List<ModelAvailable> findByAi_Id(Long aiId);
    ModelAvailable findFirstByAi_Id(Long aiId);
}
