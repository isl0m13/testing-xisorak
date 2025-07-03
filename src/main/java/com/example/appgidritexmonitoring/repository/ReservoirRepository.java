package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Reservoir;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservoirRepository extends JpaRepository<Reservoir, UUID> {
    Optional<Reservoir> findReservoirByName(String name);

    List<Reservoir> findAllByOrderByNameDesc();

}
