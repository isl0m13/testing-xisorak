package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.CrackGauge;
import com.example.appgidritexmonitoring.entity.Plumb;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlumbRepository extends JpaRepository<Plumb, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM plumb ;
                    """
    )
    List<DeviceDatasourceEnum> getAllDatasources();

    List<Plumb> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT p.*
                    FROM plumb p
                    JOIN sensor s ON p.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<Plumb> findByPlumbBySensorSerialNumber(String serialNumber);

}
