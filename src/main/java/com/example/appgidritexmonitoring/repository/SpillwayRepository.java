package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Spillway;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpillwayRepository extends JpaRepository<Spillway, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource 
                    FROM spillway;
                    """)
    List<DeviceDatasourceEnum> getAllDeviceDatasource();

    List<Spillway> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT name
                    FROM spillway
                    WHERE reservoir_id = :reservoirId
                    ORDER BY ordinal;
                    """
    )
    List<String> getNamesOfSpillwayByReservoir(UUID reservoirId);

    List<Spillway> findAllByOrderByOrdinal();


    @Query(
            nativeQuery = true,
            value = """
                    SELECT sp.*
                    FROM spillway sp
                    JOIN sensor s ON sp.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<Spillway> findSpillwayBySensorSerialNumber(String serialNumber);




}
