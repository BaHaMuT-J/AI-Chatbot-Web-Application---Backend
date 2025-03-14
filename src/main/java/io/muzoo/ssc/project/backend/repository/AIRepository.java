package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.AI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIRepository extends JpaRepository<AI, Long> {
    AI findFirstByName(String name);
    AI findFirstByNameAndVersion(String name, String version);
}
