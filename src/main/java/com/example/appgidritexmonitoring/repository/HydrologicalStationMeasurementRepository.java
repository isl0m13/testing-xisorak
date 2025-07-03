package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.HydrologicalStationMeasurement;
import com.example.appgidritexmonitoring.payload.projection.HydrologicalStationMeasurementProj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HydrologicalStationMeasurementRepository extends JpaRepository<HydrologicalStationMeasurement, UUID> {

    boolean existsByHydrologicalStationIdAndDate(UUID hydrologicalStationId, LocalDateTime date);

    @Query(
            nativeQuery = true,
            value = """
                    with temp as (SELECT MAX(date) as date,
                                         hydrological_station_id
                                  FROM hydrological_station_measurement
                                  GROUP BY hydrological_station_id)
                    SELECT hsm.*
                    FROM hydrological_station_measurement hsm
                    JOIN hydrological_station hs
                        ON hsm.hydrological_station_id = hs.id
                    JOIN temp
                        ON hsm.hydrological_station_id = temp.hydrological_station_id
                    WHERE hs.reservoir_id = :reservoirId
                      AND hsm.date = temp.date
                    ORDER BY hs.name ;
                    """
    )
    List<HydrologicalStationMeasurement> getLatestMeasurementsOfReservoir(UUID reservoirId);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT *
                    FROM hydrological_station_measurement hsm
                    JOIN hydrological_station hs ON hsm.hydrological_station_id = hs.id
                    WHERE hs.reservoir_id = :reservoirId AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC, hs.name;                    
                    """
    )
    List<HydrologicalStationMeasurement> getMeasurementsOfReservoirByDates(UUID reservoirId,
                                                                           LocalDateTime startDate,
                                                                           LocalDateTime endDate);

    @Query(nativeQuery = true,
    value = """
            with temp as (SELECT MAX(date) as date,
                                 hydrological_station_id
                          FROM hydrological_station_measurement
                          GROUP BY hydrological_station_id)
            SELECT hsm.date AS date,
                   hsm.computed_water_flow AS hydro_pressure,
                   hsm.virtual_pressure AS virtual_pressure
            FROM hydrological_station_measurement hsm
            JOIN hydrological_station hs ON hsm.hydrological_station_id = hs.id
            JOIN temp ON hsm.hydrological_station_id = temp.hydrological_station_id
            WHERE hs.reservoir_id = :reservoirId AND hsm.date = temp.date
            AND ordinal = 2 ;
            """)
    Optional<HydrologicalStationMeasurementProj> getLatestWaterLevelOfSecondStation(UUID reservoirId);

}
