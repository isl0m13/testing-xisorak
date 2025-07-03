package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {

    Optional<Sensor> findBySerialNumber(String serialNumber);



}
