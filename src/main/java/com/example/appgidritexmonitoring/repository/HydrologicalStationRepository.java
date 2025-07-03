package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.HydrologicalStation;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HydrologicalStationRepository extends JpaRepository<HydrologicalStation, UUID> {


    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM hydrological_station;
                    """
    )
    List<DeviceDatasourceEnum> getAllDeviceDatasource();

    List<HydrologicalStation> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT name
                    FROM hydrological_station
                    WHERE reservoir_id = :reservoirId
                    ORDER BY name;
                    """
    )
    List<String> getAllNamesByReservoir(UUID reservoirId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT hs.*
                    FROM hydrological_station hs
                    JOIN sensor s ON hs.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<HydrologicalStation> findHydrologicalStationBySensorSerialNumber(String serialNumber);


}
