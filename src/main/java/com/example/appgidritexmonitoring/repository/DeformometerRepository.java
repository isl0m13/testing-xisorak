package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Deformometer;
import com.example.appgidritexmonitoring.entity.Tiltmeter;
import com.example.appgidritexmonitoring.entity.WaterLevelGauge;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeformometerRepository extends JpaRepository<Deformometer, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT d.*
                    FROM deformometer d
                    JOIN sensor s ON d.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<Deformometer> findDeformometerBySensorSerialNumber(String serialNumber);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM deformometer ;
                    """
    )
    List<DeviceDatasourceEnum> getAllDatasources();

    List<Deformometer> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    List<Deformometer> findAllByReservoirIdOrderByOrdinalAscNameAsc(UUID reservoirId);


}
