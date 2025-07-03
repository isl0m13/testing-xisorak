package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Piezometer;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerCoordsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PiezometerRepository extends JpaRepository<Piezometer, UUID> {

    List<Piezometer> findAllByGateIdOrderByOrdinal(UUID gateId);
    List<Piezometer> findAllByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource 
                    FROM piezometer;
                    """)
    List<DeviceDatasourceEnum> getAllDeviceDatasource();

//    List<String> findDistinctDeviceDatasource();


    @Query(nativeQuery = true,
            value = """
                    SELECT ordinal
                    FROM piezometer p
                    WHERE p.gate_id = :gateId
                    ORDER BY ordinal ;
                    """)
    List<Integer> findOrdinalByGateId(UUID gateId);


    @Query(nativeQuery = true,
            value = """
                    SELECT  p.id as id, 
                            p.ordinal as ordinal, 
                            p.name as name, 
                            p.lat as lat, 
                            p.lon as lon
                    FROM piezometer p
                    JOIN gate g ON p.gate_id = g.id
                    WHERE g.reservoir_id = :reservoirId
                    ORDER BY p.lat, p.lon ;
                    """)
    List<PiezometerCoordsDTO> getCoordsOfPiezometerByReservoir(UUID reservoirId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT p.*
                    FROM piezometer p
                    JOIN sensor s ON p.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<Piezometer> findPiezometerBySensorSerialNumber(String serialNumber);





}
