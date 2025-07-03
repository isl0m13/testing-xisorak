package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.WaterFlowMeter;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaterFlowMeterRepository extends JpaRepository<WaterFlowMeter, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT device_datasource 
                    FROM water_flow_meter;
                    """)
    List<DeviceDatasourceEnum> getAllDeviceDatasource();

    List<WaterFlowMeter> findByDeviceDatasource(DeviceDatasourceEnum deviceDatasourceEnum);

    Optional<WaterFlowMeter> findWaterFlowMeterByPressureDatasource(String pressureDatasource);
}
