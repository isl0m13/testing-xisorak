package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Piezometer;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.WaterLevelGauge;
import com.example.appgidritexmonitoring.entity.WaterLevelGaugeMeasurement;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaterLevelGaugeRepository extends JpaRepository<WaterLevelGauge, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource 
                    FROM water_level_gauge;
                    """)
    List<DeviceDatasourceEnum> getAllDeviceDatasource();

    List<WaterLevelGauge> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    List<WaterLevelGauge> findAllByReservoirIdOrderByOrdinal(UUID reservoir_id);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT wlg.*
                    FROM water_level_gauge wlg
                    JOIN sensor s ON wlg.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<WaterLevelGauge> findWaterLevelGaugeBySensorSerialNumber(String serialNumber);

}
