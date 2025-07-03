package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.WaterLevelInVolume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WaterLevelInVolumeRepository extends JpaRepository<WaterLevelInVolume, UUID> {

    Optional<WaterLevelInVolume> findByReservoirIdAndWaterLevelEquals(UUID reservoirId, double waterLevel);
}
