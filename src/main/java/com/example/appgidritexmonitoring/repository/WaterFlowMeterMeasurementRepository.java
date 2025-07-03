package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.WaterFlowMeterMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WaterFlowMeterMeasurementRepository extends JpaRepository<WaterFlowMeterMeasurement, UUID> {


    boolean existsByWaterFlowMeterIdAndDate(UUID waterFlowMeterId, LocalDateTime date);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT *
                    FROM water_flow_meter_measurement wfmm
                    JOIN water_flow_meter wfm ON wfmm.water_flow_meter_id = wfm.id
                    WHERE reservoir_id = :reservoirId
                    ORDER BY date DESC
                    LIMIT 1   
                    """
    )
    WaterFlowMeterMeasurement getLatestMeasurementOfReservoir(UUID reservoirId);



    @Query(
            nativeQuery = true,
            value = """
                    SELECT *
                    FROM water_flow_meter_measurement wfmm
                    JOIN water_flow_meter wfm ON wfmm.water_flow_meter_id = wfm.id
                    WHERE reservoir_id = :reservoirId AND date BETWEEN :startDate and :endDate 
                    ORDER BY date DESC;
                    """)
    List<WaterFlowMeterMeasurement> getMeasurementsOfReservoirs(UUID reservoirId,
                                                                LocalDateTime startDate,
                                                                LocalDateTime endDate);

}
