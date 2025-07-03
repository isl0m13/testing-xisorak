package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.CrackGauge;
import com.example.appgidritexmonitoring.entity.CrackGaugeMeasurement;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrackGaugeRepository extends JpaRepository<CrackGauge, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM crack_gauge ;
                    """
    )
    List<DeviceDatasourceEnum> getAllDatasources();


    List<CrackGauge> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    List<CrackGauge> findAllByLocationOrderByOrdinalAscNameAsc(String locationName);


    List<CrackGauge> findByOrdinalAndLocation(Integer ordinal, String locationName);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT cg.*
                    FROM crack_gauge cg\s
                    JOIN sensor s ON cg.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<CrackGauge> findCrackGaugeBySensorSerialNumber(String serialNumber);



}
