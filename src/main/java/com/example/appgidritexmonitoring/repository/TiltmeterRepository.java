package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.entity.Plumb;
import com.example.appgidritexmonitoring.entity.Tiltmeter;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TiltmeterRepository extends JpaRepository<Tiltmeter, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM tiltmeter ;
                    """
    )
    List<DeviceDatasourceEnum> getAllDatasources();

    List<Tiltmeter> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT t.*
                    FROM tiltmeter t
                    JOIN sensor s ON t.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<Tiltmeter> findTiltmeterBySensorSerialNumber(String serialNumber);


}
