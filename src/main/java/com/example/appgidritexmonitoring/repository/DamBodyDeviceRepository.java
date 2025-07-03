package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.DamBodyDevice;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DamBodyDeviceRepository extends JpaRepository<DamBodyDevice, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource
                    FROM dam_body_device ;
                    """
    )
    List<DeviceDatasourceEnum> getAllDatasources();

    List<DamBodyDevice> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasource);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT dbd.*
                    FROM dam_body_device dbd\s
                    JOIN sensor s ON dbd.sensor_id = s.id
                    WHERE s.serial_number = :serialNumber ;
                    """
    )
    Optional<DamBodyDevice> findDamBodyDeviceBySensorSerialNumber(String serialNumber);

    Optional<DamBodyDevice> findDamBodyDeviceByName(String name);
}
