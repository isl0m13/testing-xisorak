package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.SpillwayMeasurement;
import com.example.appgidritexmonitoring.payload.projection.SpillwayTotalWaterFlowProj;
import com.example.appgidritexmonitoring.payload.spillway.SpillwayMeasurementTotalDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpillwayMeasurementRepository extends JpaRepository<SpillwayMeasurement, UUID> {

    @Query(nativeQuery = true,
            value = """
                    with temp as (SELECT max(date) as date, spillway_id
                                  FROM spillway_measurement
                                  GROUP BY spillway_id)
                                
                    SELECT sm.*
                    FROM spillway_measurement sm
                             JOIN spillway s ON s.id = sm.spillway_id
                    WHERE date = (SELECT date FROM temp where temp.spillway_id = sm.spillway_id)
                      AND s.reservoir_id = :reservoirId ;
                    """)
    List<SpillwayMeasurement> getLatestMeasurementsOfReservoir(UUID reservoirId);

    @Query(nativeQuery = true,
            value = """
                    SELECT sm.*
                    FROM spillway_measurement sm
                    WHERE sm.spillway_id = :spillwayId AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC ;
                    """)
    List<SpillwayMeasurement> getMeasurementsOfSpillway(UUID spillwayId, LocalDateTime startDate, LocalDateTime endDate);



    @Query(nativeQuery = true,
            value = """
                    SELECT sm.*
                    FROM spillway_measurement sm
                    JOIN spillway s ON sm.spillway_id = s.id
                    WHERE s.reservoir_id = :reservoirId AND date BETWEEN :startDate AND :endDate
                    ORDER BY sm.date DESC, s.ordinal ;
                    """)
    List<SpillwayMeasurement> getMeasurementsOfReservoirByDate(UUID reservoirId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(nativeQuery = true,
            value = """
                     SELECT *
                    FROM (
                             SELECT *
                             FROM spillway_measurement sm
                                    JOIN spillway s ON sm.spillway_id = s.id
                             WHERE date = (SELECT min(date)
                                           FROM spillway_measurement sm2
                                           WHERE sm2.spillway_id = sm.spillway_id AND date_trunc('days', sm2.date) = date_trunc('days', sm.date) )
                             ORDER BY sm.date DESC , s.ordinal
                         ) t
                     WHERE reservoir_id = :reservoirId AND date BETWEEN :startDate AND :endDate ;
                     """)
    List<SpillwayMeasurement> getMeasurementsOfReservoirByDateV2(UUID reservoirId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(
            nativeQuery = true,
            value = """
                    with temp as (SELECT max(date) as date, spillway_id
                                  FROM spillway_measurement
                                  GROUP BY spillway_id)
                    
                    SELECT SUM(sm.computed_water_flow) AS total_water_flow,
                           MAX(t.date) AS date
                    FROM spillway_measurement sm
                    JOIN spillway s ON s.id = sm.spillway_id
                    JOIN temp t ON t.spillway_id = sm.spillway_id AND t.date = sm.date
                    WHERE s.reservoir_id = :reservoirId ;
                    """
    )
    Optional<SpillwayTotalWaterFlowProj> getTotalMeasurementOfReservoir(UUID reservoirId);


    boolean existsBySpillwayIdAndDate(UUID spillwayId, LocalDateTime date);


}
