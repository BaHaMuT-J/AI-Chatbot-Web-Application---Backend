package io.muzoo.ssc.project.backend.repository;

import io.muzoo.ssc.project.backend.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    Temperature findFirstById(Long id);
    Temperature findFirstByUser_IdAndAi_Id(Long userId, Long aiId);
}
